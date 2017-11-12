package com.example.owner.shoppinglist;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.owner.shoppinglist.adapter.ListRecyclerAdapter;
import com.example.owner.shoppinglist.touch.ItemTouchHelperAdapter;
import com.example.owner.shoppinglist.touch.ItemTouchHelperCallback;

public class ShoppingList extends AppCompatActivity {

    public static final int REQUEST_CODE_EDIT = 1001;
    public static final String KEY_ITEM_ID = "KEY_ITEM_ID";
    private int positionToEdit = -1;
    RecyclerView recyclerList;
    Toolbar toolbar;

    private ListRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        ((ListApplication) getApplication()).openRealm();
        recyclerList = findViewById(R.id.recyclerShopping);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUpRecyclerView();
        setUpFab();

    }

    private void setUpFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddItemDialog();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shopping_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_deleteAll:
                deleteAllConfirmation();
                break;
            case R.id.action_addNew:
                Toast.makeText(this, "Add new item", Toast.LENGTH_SHORT).show();
                openAddItemActivity();
                break;
            default:
                Toast.makeText(this, "Unknown Menu Item", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteAllConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setMessage("Are you sure you wish to delete all?");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAll();
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

    private void deleteAll() {
        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
        ListRecyclerAdapter.deleteAllItems();
        adapter.notifyDataSetChanged();
        recyclerList.removeAllViewsInLayout();
    }

    private void openAddItemActivity() {
        final Intent intentAddItem = new Intent();
        intentAddItem.setClass(ShoppingList.this, AddItem.class);
        startActivity(intentAddItem);
        finish();
    }

    private void setUpRecyclerView() {
        recyclerList.setHasFixedSize(true);
        recyclerList.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ListRecyclerAdapter(this, ((ListApplication)getApplication()).getRealmList());
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback((ItemTouchHelperAdapter) adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerList);
        recyclerList.setAdapter(adapter);
    }

    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ShoppingList.this);

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.my_alert_dialog, null, false);

        final Spinner spnCategory = setSpinnerOptions(view);
        final EditText etName = view.findViewById(R.id.etName);
        final EditText etPrice = view.findViewById(R.id.etPrice);
        final EditText etDescription = view.findViewById(R.id.etDescription);
        final CheckBox cbPurchased = view.findViewById(R.id.cbPurchased);

        builder.setView(view);
        setPosNegButtons(builder);
        final AlertDialog dialog = builder.create();
        dialog.show();
        setPosButton(spnCategory, etName, etPrice, etDescription, cbPurchased, dialog);
    }

    @NonNull
    private Spinner setSpinnerOptions(View view) {
        final Spinner spnCategory = view.findViewById(R.id.spCategory);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.type_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategory.setAdapter(adapter2);
        return spnCategory;
    }

    private void setPosButton(final Spinner spnCategory, final EditText etName, final EditText etPrice, final EditText etDescription, final CheckBox cbPurchased, final AlertDialog dialog) {
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean wantToCloseDialog = false;

                if (!TextUtils.isEmpty(etName.getText())) {
                    if (!TextUtils.isEmpty(etPrice.getText())) {
                        if(!TextUtils.isEmpty(etDescription.getText())){
                            wantToCloseDialog = true;
                        } else {
                            etDescription.setError("This field cannot be empty");
                        }
                    } else {
                        etPrice.setError("This field cannot be empty");
                    }
                } else {
                    etName.setError("This field cannot be empty");
                }

                setToCloseDialog(wantToCloseDialog, etName, etDescription, etPrice, cbPurchased, spnCategory, dialog);
            }
        });
    }

    private void setToCloseDialog(Boolean wantToCloseDialog, EditText etName, EditText etDescription, EditText etPrice, CheckBox cbPurchased, Spinner spnCategory, AlertDialog dialog) {
        if(wantToCloseDialog) {
            String toEnterName = etName.getText().toString();
            String toEnterDescription = etDescription.getText().toString();
            int toEnterPrice = Integer.parseInt(etPrice.getText().toString());
            boolean toEnterPurchased = cbPurchased.isChecked();
            int toEnterCategory = spnCategory.getSelectedItemPosition();
            adapter.addItem(toEnterPurchased, toEnterPrice, toEnterCategory, toEnterName, toEnterDescription);
            dialog.dismiss();
        }
    }

    private void setPosNegButtons(AlertDialog.Builder builder) {
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }

    public void openEditActivity(int adapterPosition, String itemID) {
        positionToEdit = adapterPosition;

        Intent intentEdit = new Intent(this, AddItem.class);
        intentEdit.putExtra(KEY_ITEM_ID, itemID);
        startActivityForResult(intentEdit, REQUEST_CODE_EDIT);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((ListApplication)getApplication()).closeRealm();
    }
}
