package com.example.owner.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.owner.shoppinglist.adapter.ListRecyclerAdapter;
import com.example.owner.shoppinglist.data.Item;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class AddItem extends AppCompatActivity {

    @BindView(R.id.btnAddItem)
    Button btnAdd;
    @BindView(R.id.etAddDescription)
    EditText etDescription;
    @BindView(R.id.etAddName)
    EditText etName;
    @BindView(R.id.etAddPrice)
    EditText etPrice;
    @BindView(R.id.spAddCategory)
    Spinner spnCategory;
    @BindView(R.id.cbAddPurchased)
    CheckBox cbPurchased;


    private Item itemToEdit = null;
    private boolean isEdit = false;

    private ListRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        ((ListApplication) getApplication()).openRealm();

        ButterKnife.bind(this);

        if (getIntent().hasExtra(ShoppingList.KEY_ITEM_ID)) {
            String itemId = getIntent().getStringExtra(ShoppingList.KEY_ITEM_ID);

            itemToEdit = ((ListApplication)getApplication()).getRealmList().where(Item.class).
                    equalTo("itemID", itemId).findFirst();
            isEdit = true;
        }

        if (itemToEdit != null) {
            etDescription.setText(itemToEdit.getDescription());
            etName.setText(itemToEdit.getName());
            etPrice.setText(Integer.toString(itemToEdit.getPrice()));
            spnCategory.setDropDownHorizontalOffset(itemToEdit.getCategory());
            cbPurchased.setChecked(itemToEdit.getStatus());
            btnAdd.setText("Edit Item");
        }


        final Realm realmList = ((ListApplication)getApplication()).getRealmList();

        setUpSpinner();


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(etName.getText())) {
                    if (!TextUtils.isEmpty(etPrice.getText())) {
                        if(!TextUtils.isEmpty(etDescription.getText())){

                            String toEnterName = etName.getText().toString();
                            String toEnterDescription = etDescription.getText().toString();
                            int toEnterPrice = Integer.parseInt(etPrice.getText().toString());
                            boolean toEnterPurchased = cbPurchased.isChecked();
                            int toEnterCategory = spnCategory.getSelectedItemPosition();

                            if(isEdit) {
                                editRealmObject(toEnterName, toEnterDescription, toEnterPrice, toEnterPurchased, toEnterCategory, realmList);
                            } else {

                                realmList.beginTransaction();
                                Item newItem = realmList.createObject(Item.class, UUID.randomUUID().toString());
                                newItem.setStatus(toEnterPurchased);
                                newItem.setCategory(toEnterCategory);
                                newItem.setDescription(toEnterDescription);
                                newItem.setName(toEnterName);
                                newItem.setPrice(toEnterPrice);
                                realmList.commitTransaction();
                            }

                            openShoppingList();
                        } else {
                            etDescription.setError("This field cannot be empty");
                        }
                    } else {
                        etPrice.setError("This field cannot be empty");
                    }
                } else {
                    etName.setError("This field cannot be empty");
                }
            }
        });

    }

    private void openShoppingList() {
        final Intent intentShoppingList = new Intent(AddItem.this, ShoppingList.class);
        intentShoppingList.setClass(AddItem.this, ShoppingList.class);
        startActivity(intentShoppingList);
        finish();
    }

    private void addNewRealmObject(String toEnterName, String toEnterDescription, int toEnterPrice, boolean toEnterPurchased, int toEnterCategory, Realm realmList) {
        realmList.beginTransaction();
        Item newItem = realmList.createObject(Item.class, UUID.randomUUID().toString());
        newItem.setStatus(toEnterPurchased);
        newItem.setCategory(toEnterCategory);
        newItem.setDescription(toEnterDescription);
        newItem.setName(toEnterName);
        newItem.setPrice(toEnterPrice);
        realmList.commitTransaction();
    }

    private void editRealmObject(String toEnterName, String toEnterDescription, int toEnterPrice, boolean toEnterPurchased, int toEnterCategory, Realm realmList) {
        realmList.beginTransaction();
        itemToEdit.setStatus(toEnterPurchased);
        itemToEdit.setCategory(toEnterCategory);
        itemToEdit.setDescription(toEnterDescription);
        itemToEdit.setName(toEnterName);
        itemToEdit.setPrice(toEnterPrice);
        realmList.commitTransaction();
    }

    private void setUpSpinner() {
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.type_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategory.setAdapter(adapter2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((ListApplication)getApplication()).closeRealm();
    }
}
