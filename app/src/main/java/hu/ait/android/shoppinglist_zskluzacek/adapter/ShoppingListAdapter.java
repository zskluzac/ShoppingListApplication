package hu.ait.android.shoppinglist_zskluzacek.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import hu.ait.android.shoppinglist_zskluzacek.R;
import hu.ait.android.shoppinglist_zskluzacek.ShoppingListActivity;
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
        private LinearLayout rowLayout;


        public ViewHolder(View itemView) {
            super(itemView);

            ivCategory = (ImageView) itemView.findViewById(R.id.ivCategory);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            cbBought = (CheckBox) itemView.findViewById(R.id.cbBought);
            btnEdit = (Button) itemView.findViewById(R.id.btnEdit);
            rowLayout = (LinearLayout) itemView.findViewById(R.id.rowLayout);

        }
    }

    public void updateItem(String itemThatWasEdited, int positionToEdit) {
        ShoppingItem item = realmSL.where(ShoppingItem.class).
                equalTo(ShoppingListActivity.CREATE_TIME, itemThatWasEdited).
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

        handleCategory(holder, itemData);

        handlePriority(holder, itemData);

        gatherEditInfo(holder, itemData);

        handleBoughtChange(holder, itemData);

        handleEditButton(holder);
    }

    private void handleEditButton(final ViewHolder holder) {
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currName = shoppingItemList.get(holder.getAdapterPosition()).getItemName();
                String createTime = shoppingItemList.get(holder.getAdapterPosition()).getCreateTime();
                int currCat = shoppingItemList.get(holder.getAdapterPosition()).getItemCategory();
                float currPrice = shoppingItemList.get(holder.getAdapterPosition()).getItemPrice();
                String currDesc = shoppingItemList.get(holder.getAdapterPosition()).getItemDescription();
                int currPriority = shoppingItemList.get(holder.getAdapterPosition()).getPriority();
                ((ShoppingListActivity)context).openEditActivity(
                        holder.getAdapterPosition(), createTime, currName, currCat, currPrice,
                        currDesc, currPriority);
            }
        });
    }

    private void handleBoughtChange(ViewHolder holder, final ShoppingItem itemData) {
        holder.cbBought.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                realmSL.beginTransaction();
                itemData.setBought(isChecked);
                realmSL.commitTransaction();
            }
        });
    }

    private void gatherEditInfo(ViewHolder holder, ShoppingItem itemData) {
        holder.tvName.setText(itemData.getItemName());
        holder.tvPrice.setText(context.getString(R.string.currency_sign) + String.valueOf(itemData.getItemPrice()));
        holder.tvDescription.setText(itemData.getItemDescription());
        holder.cbBought.setChecked(itemData.isBought());
    }

    private void handlePriority(ViewHolder holder, ShoppingItem itemData) {
        switch(itemData.getPriority()) {
            case R.id.rbHigh:
                holder.rowLayout.setBackgroundColor(Color.rgb(128, 21, 21));
                break;
            case R.id.rbMedium:
                holder.rowLayout.setBackgroundColor(Color.rgb(34, 102, 102));
                break;
            case R.id.rbLow:
                holder.rowLayout.setBackgroundColor(Color.rgb(123, 159, 52));
                break;
        }
    }

    private void handleCategory(ViewHolder holder, ShoppingItem itemData) {
        switch(itemData.getItemCategory()) {
            case R.id.rbGroceries:
                holder.ivCategory.setImageResource(R.mipmap.grocery_icon);
                break;
            case R.id.rbClothing:
                holder.ivCategory.setImageResource(R.mipmap.clothes_icon);
                break;
            case R.id.rbElectronics:
                holder.ivCategory.setImageResource(R.mipmap.e_icon);
                break;
            case R.id.rbOther:
                holder.ivCategory.setImageResource(R.mipmap.other_icon);
                break;
        }
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

    public void deleteAll() {
        shoppingItemList.removeAll(shoppingItemList);
        realmSL.beginTransaction();
        realmSL.deleteAll();
        realmSL.commitTransaction();
        notifyDataSetChanged();
    }


    public void addItem(String createTime, String itemName, int itemCategory, String itemDescription,
                        float itemPrice, int itemPriority) {
        realmSL.beginTransaction();

        ShoppingItem newItem = realmSL.createObject(ShoppingItem.class, createTime);
        setItemInfo(itemName, itemCategory, itemDescription, itemPrice, itemPriority, newItem);
        realmSL.commitTransaction();

        shoppingItemList.add(0, newItem);
        notifyItemInserted(0);
    }

    private void setItemInfo(String itemName, int itemCategory, String itemDescription, float itemPrice, int itemPriority, ShoppingItem newItem) {
        newItem.setItemName(itemName);
        newItem.setItemCategory(itemCategory);
        newItem.setItemDescription(itemDescription);
        newItem.setItemPrice(itemPrice);
        newItem.setBought(false);
        newItem.setPriority(itemPriority);
    }
}
