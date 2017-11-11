package hu.ait.android.shoppinglist_zskluzacek;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import hu.ait.android.shoppinglist_zskluzacek.adapter.ShoppingListAdapter;
import hu.ait.android.shoppinglist_zskluzacek.data.ShoppingItem;
import io.realm.Realm;

public class NewItemActivity extends AppCompatActivity {

    Button btnEnter;
    ShoppingListAdapter adapter;

    EditText etName;
    EditText etPrice;
    EditText etDescription;
    RadioGroup rgCategory;
    RadioGroup rgPriority;

    private ShoppingItem itemToEdit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getIntent().hasExtra(ShoppingListActivity.CREATE_TIME)) {
            String itemName = getIntent().getStringExtra(ShoppingListActivity.CREATE_TIME);
            itemToEdit = ((ShoppingListApplication)getApplication()).getRealmSL().where(ShoppingItem.class)
                    .equalTo(ShoppingListActivity.CREATE_TIME, itemName).findFirst();
        }

        etName = (EditText) findViewById(R.id.etName);
        etPrice = (EditText) findViewById(R.id.etPrice);
        etDescription = (EditText) findViewById(R.id.etDescription);
        rgCategory = (RadioGroup) findViewById(R.id.rgCategory);
        rgPriority = (RadioGroup) findViewById(R.id.rgPriority);

        rgCategory.check(R.id.rbOther);
        rgPriority.check(R.id.rbLow);

        if(itemToEdit != null) {
            etName.setText(itemToEdit.getItemName());
            etPrice.setText(String.valueOf(itemToEdit.getItemPrice()));
            etDescription.setText(itemToEdit.getItemDescription());
            rgCategory.check(itemToEdit.getItemCategory());
            rgPriority.check(itemToEdit.getPriority());
        }

        btnEnter = (Button) findViewById(R.id.btnEnter);
        adapter = new ShoppingListAdapter(this, ((ShoppingListApplication)getApplication())
                .getRealmSL());

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etName.getText() == null || etName.getText().toString().equals("")) {
                    etName.setError("Must Enter Information");
                } else if(etPrice.getText() == null) {
                    etPrice.setError("Must Enter Information");
                } else if(etDescription.getText() == null) {
                    etDescription.setError("Must Enter Information");
                } else {
                    if (getIntent().hasExtra(ShoppingListActivity.CREATE_TIME)) {
                        ((ShoppingListApplication) getApplication()).getRealmSL().beginTransaction();

                        itemToEdit.setItemName(etName.getText().toString());
                        itemToEdit.setItemCategory(rgCategory.getCheckedRadioButtonId());
                        itemToEdit.setItemPrice(Float.valueOf(etPrice.getText().toString()));
                        itemToEdit.setItemDescription(etDescription.getText().toString());
                        itemToEdit.setPriority(rgPriority.getCheckedRadioButtonId());

                        ((ShoppingListApplication) getApplication()).getRealmSL().commitTransaction();

                        Intent intentResult = new Intent();
                        intentResult.putExtra(ShoppingListActivity.CREATE_TIME, itemToEdit.getCreateTime());
                        setResult(RESULT_OK, intentResult);
                        finish();

                    } else {
                        adapter.addItem(String.valueOf(System.currentTimeMillis()),
                                etName.getText().toString(), rgCategory.getCheckedRadioButtonId(),
                                etDescription.getText().toString(),
                                Float.valueOf(etPrice.getText().toString()),
                                rgPriority.getCheckedRadioButtonId());
                        Intent mainIntent = new Intent(NewItemActivity.this, ShoppingListActivity.class);
                        NewItemActivity.this.startActivity(mainIntent);
                        NewItemActivity.this.finish();
                    }
                }

            }
        });
    }
}
