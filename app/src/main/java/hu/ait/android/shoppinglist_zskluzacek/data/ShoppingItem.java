package hu.ait.android.shoppinglist_zskluzacek.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Zachary on 11/7/2017.
 */

public class ShoppingItem extends RealmObject {
    private String itemName;
    private String itemDescription;
    private String itemCategory;
    private float itemPrice;
    private boolean bought;


    @PrimaryKey
    private String createTime;

    public ShoppingItem() {
    }

    public ShoppingItem(String createTime, String itemName, String itemDescription, String itemCategory,
                        float itemPrice, boolean bought) {
        this.createTime = createTime;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.itemCategory = itemCategory;
        this.itemPrice = (float)(Math.round(itemPrice * 100d) / 100d);
        this.bought = bought;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public float getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(float itemPrice) {
        this.itemPrice = itemPrice;
    }

    public boolean isBought() {
        return bought;
    }

    public void setBought(boolean bought) {
        this.bought = bought;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCreateTime() {
        return createTime;
    }
}
