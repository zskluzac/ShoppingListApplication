package hu.ait.android.shoppinglist_zskluzacek;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import hu.ait.android.shoppinglist_zskluzacek.adapter.ShoppingListAdapter;
import hu.ait.android.shoppinglist_zskluzacek.touch.ItemTouchHelperCallback;

public class ShoppingListActivity extends AppCompatActivity {

    private ShoppingListAdapter adapter;
    public static final String CREATE_TIME = "createTime";
    public static final String ITEM_NAME = "itemName";
    public static final String ITEM_CAT = "itemCat";
    public static final String ITEM_PRICE = "itemPrice";
    public static final String ITEM_DESC = "itemDesc";
    public static final String ITEM_PRIORITY = "itemPriority";
    public static final int REQUEST_CODE_EDIT = 1001;

    private int positionToEdit = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        setUpToolbar();
        ((ShoppingListApplication)getApplication()).openRealm();
        RecyclerView recyclerViewSL = (RecyclerView) findViewById(R.id.recyclerSL);
        adapter = new ShoppingListAdapter(this, ((ShoppingListApplication)getApplication())
                .getRealmSL());
        setUpRecycler(recyclerViewSL);
        setUpItemTouchHelper(recyclerViewSL);
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setUpItemTouchHelper(RecyclerView recyclerViewSL) {
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerViewSL);
    }

    private void setUpRecycler(RecyclerView recyclerViewSL) {
        recyclerViewSL.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSL.setHasFixedSize(true);
        recyclerViewSL.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                Toast.makeText(this, "All Items Removed", Toast.LENGTH_SHORT).show();
                adapter.deleteAll();
                break;
            case R.id.action_new:
                Intent mainIntent = new Intent(ShoppingListActivity.this, NewItemActivity.class);
                ShoppingListActivity.this.startActivity(mainIntent);
                ShoppingListActivity.this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openEditActivity(int adapterPosition, String createTime, String itemName, int itemCat,
                                 float itemPrice, String itemDesc, int itemPriority) {
        positionToEdit = adapterPosition;

        Intent intentEdit = new Intent(this, NewItemActivity.class);
        putEditExtras(createTime, itemName, itemCat, itemPrice, itemDesc, itemPriority, intentEdit);
        startActivityForResult(intentEdit, REQUEST_CODE_EDIT);
    }

    private void putEditExtras(String createTime, String itemName, int itemCat, float itemPrice, String itemDesc, int itemPriority, Intent intentEdit) {
        intentEdit.putExtra(CREATE_TIME, createTime);
        intentEdit.putExtra(ITEM_NAME, itemName);
        intentEdit.putExtra(ITEM_CAT, itemCat);
        intentEdit.putExtra(ITEM_PRICE, itemPrice);
        intentEdit.putExtra(ITEM_DESC, itemDesc);
        intentEdit.putExtra(ITEM_PRIORITY, itemPriority);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_OK) {
            String itemNameThatWasEdited = data.getStringExtra(CREATE_TIME);

            adapter.updateItem(itemNameThatWasEdited, positionToEdit);
        } else {
            Toast.makeText(this, "Edit was Cancelled", Toast.LENGTH_LONG).show();
        }
    }
}
