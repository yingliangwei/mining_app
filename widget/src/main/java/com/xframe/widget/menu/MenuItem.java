package com.xframe.widget.menu;

import android.graphics.drawable.Drawable;

/***菜单项目实体*/
public class MenuItem {
    private String itemKey;
    /**
     * 菜单标题
     */
    private String itemTitle = null;
    /**
     * 菜单图标
     */
    private Drawable itemIcon = null;
    /**
     * 分隔线
     */
    private boolean splitLine = false;
    /**
     * 有switch视图
     */
    private boolean switchItem = false;
    /**
     * switch状态
     */
    private boolean switchChecked = false;
    /**
     * 项目状态
     */
    private boolean selected = false;
    /**
     * 项目可视状态
     */
    private boolean visibility = true;
    /**
     * 启用
     */
    private boolean enabled = true;
    /**
     * 点击的位置id
     */
    private int id;

    public interface OnMenuItemChange {
        /**
         * 项目属性改变时触发
         *
         * @param item          发送改变的菜单项
         * @param attributeName 改变的属性名称
         * @param value         对应改变的值
         */
        void onChange(MenuItem item, String attributeName, Object value);
    }

    /**
     * 菜单项改变事件侦听接口
     */
    private OnMenuItemChange onMenuItemChange = null;

    /**
     * 构建一个仅有关键字的实体
     *
     * @param key 关键字
     */
    public MenuItem(String key) {
        this.itemKey = key;
    }

    /**
     * 构建一个仅有分隔线的实体
     *
     * @param key       关键字
     * @param splitLine 是否分割线
     */
    public MenuItem(String key, boolean splitLine) {
        this.itemKey = key;
        this.splitLine = splitLine;
        this.selected = false;
    }

    /**
     * 构建含标题的实体
     *
     * @param key   关键字
     * @param title
     */
    public MenuItem(String key, String title) {
        this(key, title, null);
    }

    /**
     * 构建含标题的实体
     *
     * @param key   关键字
     * @param title
     * @param id 点击的位置
     */
    public MenuItem(String key, String title, int id) {
        this(key, title, null);
    }

    /**
     * 构建一个包含标题及图标的实体
     *
     * @param key   关键字
     * @param title 标题
     * @param icon  图标
     */
    public MenuItem(String key, String title, Drawable icon) {
        this(key, title, icon, false);
    }

    /**
     * 构建一个包含标题，图标及切换视图的实体
     *
     * @param key        关键字
     * @param title      标题
     * @param icon       图标
     * @param switchItem 含有切换视图？
     */
    public MenuItem(String key, String title, Drawable icon, boolean switchItem) {
        this(key, title, icon, switchItem, false);
    }

    /**
     * 构建一个包含标题，图标及切换视图的实体
     *
     * @param key        关键字
     * @param title      标题
     * @param icon       图标
     * @param switchItem 含有切换视图？
     * @param selected   选择状态
     */
    public MenuItem(String key, String title, Drawable icon, boolean switchItem, boolean selected) {
        this.itemKey = key;
        this.itemTitle = title;
        this.itemIcon = icon;
        this.switchItem = switchItem;
        this.selected = selected;
    }

    /**
     * 设置项目标题
     *
     * @param itemTitle
     */
    public void setItemTitle(String itemTitle) {
        if (this.isSplitLine()) {
            this.itemTitle = null;
        } else {
            if (this.itemTitle == null || this.itemTitle.length() == 0) {
                if (itemTitle != null && itemTitle.length() > 0) {
                    this.itemTitle = itemTitle;
                    if (onMenuItemChange != null) {
                        onMenuItemChange.onChange(this, "itemTitle", itemTitle);
                    }
                }
            } else if (this.itemTitle != null || this.itemTitle.length() > 0) {
                if (!this.itemTitle.equals(itemTitle)) {
                    this.itemTitle = itemTitle;
                    if (onMenuItemChange != null) {
                        onMenuItemChange.onChange(this, "itemTitle", itemTitle);
                    }
                }
            }
        }
    }

    /**
     * 获取项目标题
     */
    public String getItemTitle() {
        return itemTitle;
    }

    /**
     * 设置项目图标
     *
     * @param itemIcon
     */
    public void setItemIcon(Drawable itemIcon) {
        if (this.isSplitLine()) {
            this.itemIcon = null;
        } else {
            if (this.itemIcon == null && itemIcon != null) {
                this.itemIcon = itemIcon;
                if (onMenuItemChange != null) {
                    onMenuItemChange.onChange(this, "itemIcon", itemIcon);
                }
            } else if (this.itemIcon != null && !this.itemIcon.equals(itemIcon)) {
                this.itemIcon = itemIcon;
                if (onMenuItemChange != null) {
                    onMenuItemChange.onChange(this, "itemIcon", itemIcon);
                }
            }
        }
    }

    /**
     * 获取项目图标
     *
     * @return
     */
    public Drawable getItemIcon() {
        return itemIcon;
    }

    /**
     * 设置项目包含Switch
     *
     * @param switchItem
     */
    public void setSwitchItem(boolean switchItem) {
        if (this.isSplitLine()) {
            this.switchItem = false;    //项目为分割线是禁止拥有Switch
        } else {
            if (this.switchItem != switchItem) {
                this.switchItem = switchItem;
                if (onMenuItemChange != null) {
                    onMenuItemChange.onChange(this, "switchItem", switchItem);
                }
            }
        }
    }

    /**
     * 项目包含witch
     */
    public boolean isSwitchItem() {
        return switchItem;
    }

    /**
     * Switch选择状态
     *
     * @param switchChecked
     */
    public void setSwitchChecked(boolean switchChecked) {
        if (!this.isSwitchItem()) {
            this.switchChecked = false;
        } else {
            if (this.switchChecked != switchChecked) {
                this.switchChecked = switchChecked;
//                if (onMenuItemChange != null) {
//                    onMenuItemChange.onChange(this, "switchChecked", switchChecked);
//                }
            }
        }
    }

    /**
     * witch选择状态
     */
    public boolean isSwitchChecked() {
        return switchChecked;
    }

    /**
     * 设置项目选择状态
     *
     * @param selected
     */
    public void setSelected(boolean selected) {
        if (this.isSplitLine()) {
            this.selected = false;    //项目为分割线是禁止选择
        } else {
            if (this.selected != selected) {
                this.selected = selected;
                if (onMenuItemChange != null) {
                    onMenuItemChange.onChange(this, "selected", selected);
                }
            }
        }
    }

    /**
     * 项目选择状态
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * 项目是分隔线
     */
    public boolean isSplitLine() {
        return splitLine;
    }

    public void setItemKey(String key) {
        this.itemKey = key;
    }

    public String getItemKey() {
        return itemKey;
    }

    public void setVisibility(boolean visibility) {
        if (this.visibility != visibility) {
            this.visibility = visibility;
            if (onMenuItemChange != null) {
                onMenuItemChange.onChange(this, "visibility", visibility);
            }
        }
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setEnabled(boolean enabled) {
        if (this.isSplitLine()) {
            this.enabled = true;
        } else {
            if (this.enabled != enabled) {
                this.enabled = enabled;
                if (onMenuItemChange != null) {
                    onMenuItemChange.onChange(this, "enabled", enabled);
                }
            }
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置菜单改变侦听接口
     *
     * @param onMenuItemChange
     */
    public void setOnMenuItemChange(OnMenuItemChange onMenuItemChange) {
        this.onMenuItemChange = onMenuItemChange;
    }
}

