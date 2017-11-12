package com.example.owner.shoppinglist.adapter;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.owner.shoppinglist.ListApplication;
import com.example.owner.shoppinglist.R;
import com.example.owner.shoppinglist.ShoppingList;
import com.example.owner.shoppinglist.data.Item;
import com.example.owner.shoppinglist.touch.ItemTouchHelperAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class ListRecyclerAdapter extends RecyclerView.Adapter<ListRecyclerAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    private static List<Item> itemList;
    private Context context;
    private static Realm realmList;

    public ListRecyclerAdapter(Context context, Realm realmList) {
        this.context = context;
        this.realmList = realmList;

        itemList = new ArrayList<Item>();

        RealmResults<Item> items = realmList.where(Item.class).findAll();

        for (Item item : items){
            itemList.add(item);
        }
    }

    public void addItem(Item item){
        itemList.add(item);
    }

    public void addItem(boolean status, int price, int category, String name, String description){

        realmList.beginTransaction();
        Item newItem = realmList.createObject(Item.class,  UUID.randomUUID().toString());
        newItem.setStatus(status);
        newItem.setCategory(category);
        newItem.setDescription(description);
        newItem.setName(name);
        newItem.setPrice(price);
        realmList.commitTransaction();

        itemList.add(0, newItem);
        notifyItemInserted(0);
    }

    public static void deleteAllItems(){
        realmList.beginTransaction();
        realmList.delete(Item.class);
        realmList.commitTransaction();

        itemList.clear();

    }


    @Override
    public void onItemDismiss(int position) {

        Item itemToDelete = itemList.get(position);

        realmList.beginTransaction();
        itemToDelete.deleteFromRealm();
        notifyDataSetChanged();
        realmList.commitTransaction();

        itemList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, itemList.size());

    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(itemList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(itemList, i, i - 1);
            }
        }

        notifyItemMoved(fromPosition, toPosition);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName;
        private CheckBox cbBought;
        private ImageView ivItem;
        private TextView tvPrice;
        private TextView tvDetails;
        private ToggleButton tbDetails;
        private Button btnEdit;
        private Button btnDelete;


        public ViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            cbBought = itemView.findViewById(R.id.cbBought);
            ivItem = itemView.findViewById(R.id.ivItem);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            tbDetails = itemView.findViewById(R.id.tbDetails);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listRow = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_row, parent, false);
        return new ViewHolder(listRow);
    }

    @Override
    public void onBindViewHolder(final ListRecyclerAdapter.ViewHolder holder, final int position) {

        final Item itemData = itemList.get(position);

        setUpListRow(holder, itemData);
        updateIsBought(holder, itemData);
        setUpImageRes(holder, itemData);
        showDetails(holder);
        setUpBtnDelete(holder, position);

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((ShoppingList)context).openEditActivity(
                        holder.getAdapterPosition(),
                        itemList.get(holder.getAdapterPosition()).getItemID()
                );
            }
        });

    }

    private void setUpListRow(ViewHolder holder, Item itemData) {
        holder.tvName.setText(itemData.getName());
        holder.tvDetails.setText(itemData.getDescription());
        holder.tvPrice.setText(Integer.toString(itemData.getPrice()));
        holder.cbBought.setChecked(itemData.getStatus());
    }

    private void updateIsBought(ViewHolder holder, final Item itemData) {
        holder.cbBought.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                realmList.beginTransaction();
                itemData.setStatus(isChecked);
                realmList.commitTransaction();
            }
        });
    }

    private void setUpImageRes(ViewHolder holder, Item itemData) {
        switch (itemData.getCategory()) {
            case Item.BOOK:
                holder.ivItem.setImageResource(R.drawable.icon4);
                break;
            case Item.ELECTRONIC:
                holder.ivItem.setImageResource(R.drawable.icon3);
                break;
            case Item.FOOD:
                holder.ivItem.setImageResource(R.drawable.icon1);
                break;
            default:
                holder.ivItem.setImageResource(R.drawable.icon2);
        }


        final Animation sendAnim = AnimationUtils.loadAnimation(ListApplication.getAppContext(),
                R.anim.icon_anim);

        sendAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        });

        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                holder.ivItem,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        scaleDown.setDuration(310);
        scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
        scaleDown.start();

        holder.ivItem.startAnimation(sendAnim);
    }

    private void showDetails(final ViewHolder holder) {
        holder.tbDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.tbDetails.isChecked()) {
                    holder.tvDetails.setVisibility(View.VISIBLE);
                } else {
                    holder.tvDetails.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setUpBtnDelete(ViewHolder holder, final int position) {
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                builder.setMessage(R.string.confirm_delete);
                builder.setPositiveButton(R.string.confirm,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onItemDismiss(position);
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


}
