package hu.ait.android.shoppinglist_zskluzacek.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import hu.ait.android.shoppinglist_zskluzacek.NewItemActivity;
import hu.ait.android.shoppinglist_zskluzacek.R;
import hu.ait.android.shoppinglist_zskluzacek.ShoppingListActivity;
import hu.ait.android.shoppinglist_zskluzacek.ShoppingListApplication;
import hu.ait.android.shoppinglist_zskluzacek.data.ShoppingItem;
import hu.ait.android.shoppinglist_zskluzacek.touch.ItemTouchHelperAdapter;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Zachary on 11/7/2017.
 */

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    private List<ShoppingItem> shoppingItemList;
    private Context context;
    private Realm realmSL;

    public ShoppingListAdapter(Context context, Realm realmSL) {
        this.context = context;
        this.realmSL = realmSL;

        shoppingItemList = new ArrayList<ShoppingItem>();
        RealmResults<ShoppingItem> shoppingItems = realmSL.where(ShoppingItem.class).findAll()
                .sort("itemName", Sort.ASCENDING);

        for(ShoppingItem item : shoppingItems) {
            shoppingItemList.add(item);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivCategory;
        private TextView tvName;
        private TextView tvPrice;
        private TextView tvDescription;
        private CheckBox cbBought;
        private Button btnEdit;


        public ViewHolder(View itemView) {
            super(itemView);

            ivCategory = (ImageView) itemView.findViewById(R.id.ivCategory);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            cbBought = (CheckBox) itemView.findViewById(R.id.cbBought);
            btnEdit = (Button) itemView.findViewById(R.id.btnEdit);

        }
    }

    public void updateItem(String itemThatWasEdited, int positionToEdit) {
        ShoppingItem item = realmSL.where(ShoppingItem.class).
                equalTo(ShoppingListActivity.ITEM_NAME, itemThatWasEdited).
                findFirst();

        shoppingItemList.set(positionToEdit, item);

        notifyItemChanged(positionToEdit);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row,
                parent, false);
        return new ViewHolder(itemRow);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ShoppingItem itemData = shoppingItemList.get(position);

        holder.ivCategory.setImageResource(R.mipmap.ic_launcher_round);
        holder.tvName.setText(itemData.getItemName());
        holder.tvPrice.setText(String.valueOf(itemData.getItemPrice()));
        holder.tvDescription.setText(itemData.getItemDescription());
        holder.cbBought.setChecked(itemData.isBought());

        holder.cbBought.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                realmSL.beginTransaction();
                itemData.setBought(isChecked);
                realmSL.commitTransaction();
            }
        });

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currName = shoppingItemList.get(holder.getAdapterPosition()).getItemName();
                String currCat = shoppingItemList.get(holder.getAdapterPosition()).getItemCategory();
                float currPrice = shoppingItemList.get(holder.getAdapterPosition()).getItemPrice();
                String currDesc = shoppingItemList.get(holder.getAdapterPosition()).getItemDescription();
                ((ShoppingListActivity)context).openEditActivity(
                        holder.getAdapterPosition(), currName, currCat, currPrice, currDesc);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shoppingItemList.size();
    }

    @Override
    public void onItemDismiss(int position) {
        ShoppingItem itemToDelete = shoppingItemList.get(position);
        shoppingItemList.remove(position);
        realmSL.beginTransaction();
        itemToDelete.deleteFromRealm();
        realmSL.commitTransaction();
        notifyItemRemoved(position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
    }


    public void addItem(String itemName, String itemCategory, String itemDescription,
                        float itemPrice) {
        realmSL.beginTransaction();

        ShoppingItem newItem = realmSL.createObject(ShoppingItem.class, itemName);
        newItem.setItemCategory(itemCategory);
        newItem.setItemDescription(itemDescription);
        newItem.setItemPrice(itemPrice);
        newItem.setBought(false);
        realmSL.commitTransaction();

        shoppingItemList.add(0, newItem);
        notifyItemInserted(0);
    }

}
