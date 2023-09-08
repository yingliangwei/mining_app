package com.xframe.widget.menu;

import static android.widget.ListPopupWindow.MATCH_PARENT;
import static android.widget.ListPopupWindow.WRAP_CONTENT;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Space;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.cardview.widget.CardView;

/**
 * 弹出菜单类
 */
public class DropMenu {
    private final static String TAG = "DropMenu";
    /**
     * 弹出式窗体对象
     */
    private PopupWindow mPopupWindow = null;
    /**
     * 弹出式窗口锚定视图
     */
    private View mAnchor = null;

    private int position = 0;
    /**
     * 对齐方式
     */
    private int mGravity = Gravity.NO_GRAVITY;
    /**
     * 弹出式串口相对锚定视图偏移量
     */
    private int xOffset = 0, yOffset = 0;
    /**
     * 装载菜单项的容器
     */
    private LinearLayout mContainerLayout = null;
    private Context mContext;
    /**
     * 菜单宽度
     */
    private float menuWidth;
    /**
     * 菜单高度
     */
    private float menuHeight;
    /**
     * 菜单项目偏移量
     */
    private float offset;
    private float iconSize;
    private float spaceWidth;
    private float itemHeight;

    public interface OnMenuItemClickListener {
        /**
         * 菜单项单击事件
         *
         * @param item 单击的菜单项
         * @param view 单击的视图
         */
        default void onMenuItemClick(MenuItem item, View view) {

        }

        default void onMenuItemClick(MenuItem item, View view, int position) {

        }
    }

    public interface OnSwitchCheckedChangeListener {
        /**
         * 当复合按钮的选中状态更改时调用。
         *
         * @param item       复选按钮所在的菜单项
         * @param buttonView 状态已更改的复合按钮视图。
         * @param isChecked  按钮视图的新选中状态。
         */
        void onCheckedChanged(MenuItem item, CompoundButton buttonView, boolean isChecked);
    }

    /**
     * 当用户从菜单中选择项目时将收到通知的侦听器。
     */
    private OnMenuItemClickListener menuItemClickListener = null;
    /**
     * 当用户改变复选按钮状态时将收到的通知的侦听器
     */
    private OnSwitchCheckedChangeListener switchCheckedChangeListener = null;

    /**
     * 构造函数创建带有定位视图的新弹出菜单。
     *
     * @param context 弹出菜单运行在上下文中，通过它可以访问当前主题、资源等。
     * @param anchor  此弹出窗口的锚定视图。如果有空间，弹出窗口将显示在锚点下方，如果没有空间，弹出窗口将显示在锚点上方。
     */
    public DropMenu(@NonNull Context context, @NonNull View anchor) {
        this(context, anchor, Gravity.NO_GRAVITY);
    }

    /**
     * 造函数创建带有定位视图的新弹出菜单。
     *
     * @param context 弹出菜单运行在上下文中，通过它可以访问当前主题、资源等。
     * @param anchor  此弹出窗口的锚定视图。
     * @param gravity 弹出窗口相对于锚定的对齐
     */
    public DropMenu(@NonNull Context context, @NonNull View anchor, int gravity) {
        this(context, anchor, 0, 0, gravity);
    }

    /**
     * 造函数创建带有定位视图的新弹出菜单。
     *
     * @param context 弹出菜单运行在上下文中，通过它可以访问当前主题、资源等。
     * @param anchor  此弹出窗口的锚定视图。
     * @param xoff    相对于定位点的水平偏移（以像素为单位）
     * @param yoff    相对于定位点的垂直偏移（以像素为单位）
     */
    public DropMenu(@NonNull Context context, @NonNull View anchor, int xoff, int yoff) {
        this(context, anchor, 0, 0, Gravity.NO_GRAVITY);
    }

    /**
     * 造函数创建带有定位视图的新弹出菜单。
     *
     * @param context 弹出菜单运行在上下文中，通过它可以访问当前主题、资源等。
     * @param anchor  此弹出窗口的锚定视图。
     * @param xoff    相对于定位点的水平偏移（以像素为单位）
     * @param yoff    相对于定位点的垂直偏移（以像素为单位）
     * @param gravity 弹出窗口相对于锚定的对齐
     */
    public DropMenu(@NonNull Context context, @NonNull View anchor, int xoff, int yoff, int gravity) {
        this.mContext = context;
        this.mAnchor = anchor;
        this.xOffset = xoff;
        this.yOffset = yoff;
        this.mGravity = gravity;
        this.menuWidth = dp2px(context, 100);
        //预留阴影区域
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.menuHeight = dp2px(context, 5);
        } else {
            this.menuHeight = dp2px(context, 1.5f);
        }
        this.itemHeight = dp2px(context, 32);
        this.offset = dp2px(context, 5);
        this.iconSize = dp2px(context, 24);
        this.spaceWidth = dp2px(context, 32);
        createPopup();
    }
    /******************************菜单项内容*********************************/
    /**
     * 添加菜单项
     *
     * @param titleResId 标题
     */
    public boolean addMenuItem(String key, @StringRes int titleResId) {
        return addMenuItem(key, titleResId, 0);
    }

    /**
     * 添加菜单项
     *
     * @param titleResId 标题
     * @param resIconId  图标ID
     */
    public boolean addMenuItem(String key, @StringRes int titleResId, @DrawableRes int resIconId) {
        return addMenuItem(key, titleResId, resIconId, false, false);
    }

    /**
     * 添加菜单项
     *
     * @param titleResId        标题
     * @param resIconId         图标ID
     * @param switchItem        包含Switch
     * @param switchItemChecked 如果switchItem不为null，该参数有效
     */
    public boolean addMenuItem(String key, @StringRes int titleResId, @DrawableRes int resIconId, boolean switchItem, boolean switchItemChecked) {
        return addMenuItem(key, titleResId, resIconId, switchItem, switchItemChecked, false);
    }

    /**
     * 添加菜单项
     *
     * @param titleResId        标题
     * @param resIconId         图标ID
     * @param switchItem        包含Switch
     * @param switchItemChecked 如果switchItem不为null，该参数有效
     * @param selectedItem      项目选择状态
     */
    public boolean addMenuItem(String key, @StringRes int titleResId, @DrawableRes int resIconId, boolean switchItem, boolean switchItemChecked, boolean selectedItem) {
        return addMenuItem(key, mContext.getResources().getString(titleResId), mContext.getResources().getDrawable(resIconId), switchItem, switchItemChecked, selectedItem);
    }

    /**
     * 添加菜单项
     *
     * @param title     标题
     * @param resIconId 图标ID
     */
    public boolean addMenuItem(String key, String title, @DrawableRes int resIconId) {
        return addMenuItem(key, title, resIconId, false, false);
    }

    /**
     * 添加菜单项
     *
     * @param title             标题
     * @param resIconId         图标ID
     * @param switchItem        包含Switch
     * @param switchItemChecked 如果switchItem不为null，该参数有效
     */
    public boolean addMenuItem(String key, String title, @DrawableRes int resIconId, boolean switchItem, boolean switchItemChecked) {
        return addMenuItem(key, title, resIconId, switchItem, switchItemChecked, false);
    }

    /**
     * 添加菜单项
     *
     * @param title             标题
     * @param resIconId         图标ID
     * @param switchItem        包含Switch
     * @param switchItemChecked 如果switchItem不为null，该参数有效
     * @param selectedItem      项目选择状态
     */
    public boolean addMenuItem(String key, String title, @DrawableRes int resIconId, boolean switchItem, boolean switchItemChecked, boolean selectedItem) {
        return addMenuItem(key, title, mContext.getResources().getDrawable(resIconId), switchItem, switchItemChecked, selectedItem);
    }

    /**
     * 添加菜单项
     *
     * @param title 标题
     */
    public boolean addMenuItem(String key, String title) {
        return addMenuItem(key, title, null);
    }

    /**
     * 添加菜单项
     *
     * @param title 标题
     * @param icon  图标
     */
    public boolean addMenuItem(String key, String title, Drawable icon) {
        return addMenuItem(key, title, icon, false, false);
    }

    /**
     * 添加菜单项
     *
     * @param title             标题
     * @param icon              图标
     * @param switchItem        包含Switch
     * @param switchItemChecked 如果switchItem不为null，该参数有效
     */
    public boolean addMenuItem(String key, String title, Drawable icon, boolean switchItem, boolean switchItemChecked) {
        return addMenuItem(key, title, icon, switchItem, switchItemChecked, false);
    }

    /**
     * 添加菜单项
     *
     * @param title             标题
     * @param icon              图标
     * @param switchItem        包含Switch
     * @param switchItemChecked 如果switchItem不为null，该参数有效
     * @param selectedItem      项目选择状态
     */
    public boolean addMenuItem(String key, String title, Drawable icon, boolean switchItem,
                               boolean switchItemChecked, boolean selectedItem) {
        return addMenuItem(-1, key, title, icon, switchItem, switchItemChecked, selectedItem);
    }

    /**
     * 添加菜单项到指定位置
     *
     * @param index     位置(&lt;0 || &gt;=size()表示添加到列表末尾）
     * @param title     标题
     * @param resIconId 图标
     */
    public boolean addMenuItem(int index, String key, String title, @DrawableRes int resIconId) {
        return addMenuItem(index, key, title, resIconId, false, false);
    }

    /**
     * 添加菜单项到指定位置
     *
     * @param index             位置(&lt;0 || &gt;=size()表示添加到列表末尾）
     * @param title             标题
     * @param resIconId         图标
     * @param switchItem        包含Switch
     * @param switchItemChecked 如果switchItem不为null，该参数有效
     */
    public boolean addMenuItem(int index, String key, String title, @DrawableRes int resIconId, boolean switchItem, boolean switchItemChecked) {
        return addMenuItem(index, key, title, resIconId, switchItem, switchItemChecked, false);
    }

    /**
     * 添加菜单项到指定位置
     *
     * @param index             位置(&lt;0 || &gt;=size()表示添加到列表末尾）
     * @param title             标题
     * @param resIconId         图标
     * @param switchItem        包含Switch
     * @param switchItemChecked 如果switchItem不为null，该参数有效
     * @param selectedItem      项目选择状态
     */
    public boolean addMenuItem(int index, String key, String title, @DrawableRes int resIconId,
                               boolean switchItem, boolean switchItemChecked, boolean selectedItem) {
        return addMenuItem(index, key, title, mContext.getResources().getDrawable(resIconId),
                switchItem, switchItemChecked, selectedItem);
    }

    /**
     * 添加菜单项到指定位置
     *
     * @param index 位置(&lt;0 || &gt;=size()表示添加到列表末尾）
     * @param title 标题
     */
    public boolean addMenuItem(int index, String key, String title) {
        return addMenuItem(index, key, title, null);
    }

    /**
     * 添加菜单项到指定位置
     *
     * @param index 位置(&lt;0 || &gt;=size()表示添加到列表末尾）
     * @param title 标题
     * @param icon  图标
     */
    public boolean addMenuItem(int index, String key, String title, Drawable icon) {
        return addMenuItem(index, key, title, icon, false, false);
    }

    /**
     * 添加菜单项到指定位置
     *
     * @param index             位置(&lt;0 || &gt;=size()表示添加到列表末尾）
     * @param key               菜单项关键字
     * @param title             标题
     * @param icon              图标
     * @param switchItem        包含Switch
     * @param switchItemChecked 如果switchItem不为null，该参数有效
     */
    public boolean addMenuItem(int index, String key, String title, Drawable icon, boolean switchItem, boolean switchItemChecked) {
        return addMenuItem(index, key, title, icon, switchItem, switchItemChecked, false);
    }

    /**
     * 添加菜单项到指定位置
     *
     * @param index             位置(&lt;0 || &gt;=size()表示添加到列表末尾）
     * @param key               菜单项关键字
     * @param title             标题
     * @param icon              图标
     * @param switchItem        包含Switch
     * @param switchItemChecked 如果switchItem不为null，该参数有效
     * @param selectedItem      项目选择状态
     */
    public boolean addMenuItem(int index, String key, String title, Drawable icon, boolean switchItem,
                               boolean switchItemChecked, boolean selectedItem) {
        MenuItem menuItem = new MenuItem(key, title, icon, switchItem, selectedItem);
        menuItem.setSwitchChecked(switchItemChecked);
        return addMenuItem(index, menuItem);
    }

    /**
     * 添加菜单项到菜单尾部
     *
     * @param menuItem
     */
    public boolean addMenuItem(MenuItem menuItem) {
        return addMenuItem(-1, menuItem);
    }

    /**
     * 添加菜单项到指定位置
     *
     * @param index
     * @param menuItem
     */
    public boolean addMenuItem(int index, MenuItem menuItem) {
        if (isMenuItemKeyOnly(menuItem.getItemKey())) {
            if (menuItem.isSelected()) {
                for (int pos = 0; pos < mContainerLayout.getChildCount(); pos++) {
                    MenuItem item = (MenuItem) mContainerLayout.getChildAt(pos).getTag();
                    if (item != null) {
                        item.setSelected(false);
                    }
                }
            }
            createMenuItemView(index, menuItem);
            menuItem.setOnMenuItemChange(menuItemChange);  //设置菜单改变事件侦听
            return true;
        } else {
            return false;
        }
    }

    /**
     * 添加菜单分割线
     */
    public boolean addSplitLine() {
        return addSplitLine(-1);
    }

    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * 添加菜单分割线到指定位置
     *
     * @param index 位置索引
     * @return
     */
    public boolean addSplitLine(int index) {
        String key = "splitLine";
        int id = 0;
        //生成唯一关键字
        do {
            id++;
        } while (!isMenuItemKeyOnly(key + id));
        return addSplitLine(key + id, index);
    }

    /**
     * 添加菜单分割线
     *
     * @param key 关键字
     * @return
     */
    public boolean addSplitLine(String key) {
        return addSplitLine(key, -1);
    }

    /**
     * 添加菜单分割线到指定位置
     *
     * @param key   关键字
     * @param index 位置索引
     * @return
     */
    public boolean addSplitLine(String key, int index) {
        if (isMenuItemKeyOnly(key)) {    //检测关键字是否唯一
            MenuItem menuItem = new MenuItem(key, true);
            menuItem.setVisibility(true);
            menuItem.setEnabled(false);
            createMenuItemView(index, menuItem);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除指定菜单项
     *
     * @param index 需要上传的菜单索引
     * @return
     */
    public boolean removeMenuItem(int index) {
        if (index >= 0 && index < mContainerLayout.getChildCount()) {
            MenuItem item = (MenuItem) mContainerLayout.getChildAt(index).getTag();
            if (item != null) {
                //调整项目删除后菜单的整体高度
                if (item.isSplitLine()) {
                    menuHeight -= 2 + offset * 2;
                } else {
                    menuHeight -= itemHeight + offset * 2;
                }
                mContainerLayout.removeViewAt(index);
                return true;
            }
        }
        return false;
    }

    /**
     * 删除指定菜单项
     *
     * @param menuItem
     * @return
     */
    public boolean removeMenuItem(MenuItem menuItem) {
        if (menuItem != null) {
            for (int i = 0; i < mContainerLayout.getChildCount(); i++) {
                if (menuItem.equals(mContainerLayout.getChildAt(i).getTag())) {
                    return removeMenuItem(i);
                }
            }
        }
        return false;
    }

    /**
     * 设置菜单项目单击事件侦听器
     *
     * @param listener
     */
    public void setOnMenuItemClickListener(OnMenuItemClickListener listener) {
        this.menuItemClickListener = listener;
    }

    /**
     * 设置切换按钮复选状态改变事件侦听器
     *
     * @param listener
     */
    public void setOnSwitchCheckedChangeListener(OnSwitchCheckedChangeListener listener) {
        this.switchCheckedChangeListener = listener;
    }

    /**
     * 隐藏或显示指定菜单项
     *
     * @param index      需要执行显示/隐藏的菜单索引位置
     * @param visibility 显示
     */
    public boolean setMenuItemVisibility(int index, boolean visibility) {
        if (index >= 0 && index < mContainerLayout.getChildCount()) {
            MenuItem menuItem = (MenuItem) mContainerLayout.getChildAt(index).getTag();
            if (menuItem != null) {
                if (menuItem.isVisibility() != visibility) {
                    menuItem.setVisibility(visibility);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 启用菜单项
     *
     * @param index
     * @param enabled
     * @return
     */
    public boolean setMenuItemEnabled(int index, boolean enabled) {
        if (index >= 0 && index < mContainerLayout.getChildCount()) {
            MenuItem menuItem = (MenuItem) mContainerLayout.getChildAt(index).getTag();
            if (menuItem != null) {
                if (!menuItem.isSplitLine()) {
                    if (menuItem.isEnabled() != enabled) {
                        menuItem.setEnabled(enabled);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 设置指定项菜单选择状态
     *
     * @param index
     * @param selected
     * @return
     */
    public boolean setMenuItemSelected(int index, boolean selected) {
        if (index >= 0 && index < mContainerLayout.getChildCount()) {
            MenuItem menuItem = (MenuItem) mContainerLayout.getChildAt(index).getTag();
            if (menuItem != null) {
                if (!menuItem.isSplitLine()) {
                    if (menuItem.isSelected() != selected) {
                        menuItem.setSelected(selected);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 设置菜单项图标
     *
     * @param index
     * @param resId
     * @return
     */
    public boolean setMenuItemIcon(int index, @DrawableRes int resId) {
        return setMenuItemIcon(index, mContext.getResources().getDrawable(resId));
    }

    /**
     * 设置菜单项图标
     *
     * @param index
     * @param icon
     * @return
     */
    public boolean setMenuItemIcon(int index, Drawable icon) {
        if (index >= 0 && index < mContainerLayout.getChildCount()) {
            MenuItem menuItem = (MenuItem) mContainerLayout.getChildAt(index).getTag();
            if (menuItem != null) {
                if (!menuItem.isSplitLine()) {
                    if (!menuItem.getItemIcon().equals(icon)) {
                        menuItem.setItemIcon(icon);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 设置菜单标题
     *
     * @param index
     * @param title
     * @return
     */
    public boolean setMenuItemTitle(int index, String title) {
        if (index >= 0 && index < mContainerLayout.getChildCount()) {
            MenuItem menuItem = (MenuItem) mContainerLayout.getChildAt(index).getTag();
            if (menuItem != null) {
                if (!menuItem.isSplitLine()) {
                    if (!menuItem.getItemTitle().equals(title)) {
                        menuItem.setItemTitle(title);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 设置菜单项复选
     *
     * @param index
     * @param _switch
     * @return
     */
    public boolean setMenuItemSwitch(int index, boolean _switch) {
        if (index >= 0 && index < mContainerLayout.getChildCount()) {
            MenuItem menuItem = (MenuItem) mContainerLayout.getChildAt(index).getTag();
            if (menuItem != null) {
                if (!menuItem.isSplitLine()) {
                    if (menuItem.isSwitchItem() != _switch) {
                        menuItem.setSwitchItem(_switch);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 设置菜单项背景资源
     *
     * @param resid 资源ID
     */
    public void setMenuItemBackgroundResource(@DrawableRes int resid) {
        for (int index = 0; index < mContainerLayout.getChildCount(); index++) {
            mContainerLayout.getChildAt(index).setBackgroundResource(resid);
        }
    }

    /**
     * 设置指定菜单项背景资源
     *
     * @param resid 资源ID
     * @param index 菜单索引
     */
    public void setMenuItemBackgroundResource(@DrawableRes int resid, int index) {
        mContainerLayout.getChildAt(index).setBackgroundResource(resid);
    }

    /**
     * 设置菜单项背景图
     *
     * @param background 背景图Drawable对象
     */
    public void setMenuItemBackgroundDrawable(Drawable background) {
        for (int index = 0; index < mContainerLayout.getChildCount(); index++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mContainerLayout.getChildAt(index).setBackground(background);
            } else {
                mContainerLayout.getChildAt(index).setBackgroundDrawable(background);
            }
        }
    }

    /**
     * 设置菜单项背景图
     *
     * @param background 背景图Drawable对象
     * @param index      菜单索引
     */
    public void setMenuItemBackgroundDrawable(Drawable background, int index) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mContainerLayout.getChildAt(index).setBackground(background);
        } else {
            mContainerLayout.getChildAt(index).setBackgroundDrawable(background);
        }
    }

    /**
     * 设置菜单项目背景颜色
     *
     * @param color
     */
    public void setMenuItemBackgroundColor(@ColorInt int color) {
        for (int index = 0; index < mContainerLayout.getChildCount(); index++) {
            mContainerLayout.getChildAt(index).setBackgroundColor(color);
        }
    }

    /**
     * 设置菜单项目背景颜色
     *
     * @param color
     * @param index 菜单索引
     */
    public void setMenuItemBackgroundColor(@ColorInt int color, int index) {
        mContainerLayout.getChildAt(index).setBackgroundColor(color);
    }

    /**
     * 设置分割线背景资源id
     *
     * @param resid
     */
    public void setSplitLineResource(@DrawableRes int resid) {
        for (int index = 0; index < mContainerLayout.getChildCount(); index++) {
            LinearLayout menuItemView = (LinearLayout) mContainerLayout.getChildAt(index);
            MenuItem item = (MenuItem) menuItemView.getTag();
            if (item != null) {
                if (item.isSplitLine() && menuItemView.getChildCount() > 0) {
                    menuItemView.getChildAt(0).setBackgroundResource(resid);
                }
            }
        }
    }

    /**
     * 设置分割线背景图像
     *
     * @param drawable
     */
    public void setSplitLineDrawable(Drawable drawable) {
        for (int index = 0; index < mContainerLayout.getChildCount(); index++) {
            LinearLayout menuItemView = (LinearLayout) mContainerLayout.getChildAt(index);
            MenuItem item = (MenuItem) menuItemView.getTag();
            if (item != null) {
                if (item.isSplitLine() && menuItemView.getChildCount() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        menuItemView.getChildAt(0).setBackground(drawable);
                    } else {
                        menuItemView.getChildAt(0).setBackgroundDrawable(drawable);
                    }
                }
            }
        }
    }

    /**
     * 设置分割线颜色
     *
     * @param color
     */
    public void setSplitLineColor(@ColorInt int color) {
        for (int index = 0; index < mContainerLayout.getChildCount(); index++) {
            LinearLayout menuItemView = (LinearLayout) mContainerLayout.getChildAt(index);
            MenuItem item = (MenuItem) menuItemView.getTag();
            if (item != null) {
                if (item.isSplitLine() && menuItemView.getChildCount() > 0) {
                    menuItemView.getChildAt(0).setBackgroundColor(color);
                }
            }
        }
    }

    /**
     * 设置菜单项标题颜色
     *
     * @param color
     */
    public void setMenuItemTextColor(@ColorInt int color) {
        for (int index = 0; index < mContainerLayout.getChildCount(); index++) {
            LinearLayout menuItemView = (LinearLayout) mContainerLayout.getChildAt(index);
            MenuItem item = (MenuItem) menuItemView.getTag();
            if (item != null) {
                if (!item.isSplitLine() && menuItemView.getChildCount() > 0) {
                    for (int i = 0; i < menuItemView.getChildCount(); i++) {
                        if (menuItemView.getChildAt(i) instanceof TextView) {
                            ((TextView) menuItemView.getChildAt(i)).setTextColor(color);
                        }
                    }
                }
            }
        }
    }

    /**
     * 设置菜单项标题颜色
     *
     * @param colors
     */
    public void setMenuItemTextColor(ColorStateList colors) {
        for (int index = 0; index < mContainerLayout.getChildCount(); index++) {
            LinearLayout menuItemView = (LinearLayout) mContainerLayout.getChildAt(index);
            MenuItem item = (MenuItem) menuItemView.getTag();
            if (item != null) {
                if (!item.isSplitLine() && menuItemView.getChildCount() > 0) {
                    for (int i = 0; i < menuItemView.getChildCount(); i++) {
                        if (menuItemView.getChildAt(i) instanceof TextView) {
                            ((TextView) menuItemView.getChildAt(i)).setTextColor(colors);
                        }
                    }
                }
            }
        }
    }

    /**
     * 设置菜单项图标过滤色
     *
     * @param color
     */
    public void setMenuItemIconTint(@ColorInt int color) {
        for (int index = 0; index < mContainerLayout.getChildCount(); index++) {
            LinearLayout menuItemView = (LinearLayout) mContainerLayout.getChildAt(index);
            MenuItem item = (MenuItem) menuItemView.getTag();
            if (item != null) {
                if (!item.isSplitLine() && menuItemView.getChildCount() > 0) {
                    for (int i = 0; i < menuItemView.getChildCount(); i++) {
                        if (menuItemView.getChildAt(i) instanceof ImageView) {
                            ((ImageView) menuItemView.getChildAt(i)).setColorFilter(color, PorterDuff.Mode.SRC_IN);
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取菜单宽度（菜单项添加完后获取）
     */
    public float getMenuWidth() {
        return menuWidth;
    }

    /**
     * 获取菜单高度（菜单项添加完后获取）
     */
    public float getMenuHeight() {
        return menuHeight;
    }

    /**
     * 获取菜项高度
     */
    public float getItemHeight() {
        return itemHeight;
    }

    /**
     * 显示弹出菜单
     */
    public void show() {
        if (mPopupWindow != null) {
            mPopupWindow.setHeight((int) menuHeight);
            mPopupWindow.setWidth((int) menuWidth);
            if (xOffset == 0 && yOffset == 0 && mGravity == Gravity.NO_GRAVITY) {
                mPopupWindow.showAsDropDown(mAnchor);
            } else {
                int xoff, yoff;
                if (mGravity == Gravity.LEFT || mGravity == Gravity.START
                        || mGravity == (Gravity.LEFT | Gravity.BOTTOM)
                        || mGravity == (Gravity.START | Gravity.BOTTOM)) {    //锚定左下
                    mPopupWindow.showAsDropDown(mAnchor, xOffset, yOffset);
                } else if (mGravity == Gravity.RIGHT || mGravity == Gravity.END
                        || mGravity == (Gravity.RIGHT | Gravity.BOTTOM)
                        || mGravity == (Gravity.END | Gravity.BOTTOM)) { //锚定右下
                    xoff = (xOffset + -(int) (menuWidth - mAnchor.getWidth()));
                    mPopupWindow.showAsDropDown(mAnchor, xoff, yOffset);
                } else if (mGravity == (Gravity.LEFT | Gravity.TOP)
                        || mGravity == (Gravity.START | Gravity.TOP)) {   //左上
                    yoff = yOffset + -(int) (menuHeight + mAnchor.getHeight());
                    mPopupWindow.showAsDropDown(mAnchor, xOffset, yoff);
                } else if (mGravity == (Gravity.RIGHT | Gravity.TOP)
                        || mGravity == (Gravity.END | Gravity.TOP)) {  //右上
                    xoff = (xOffset + -(int) (menuWidth - mAnchor.getWidth()));
                    yoff = yOffset + -(int) (menuHeight + mAnchor.getHeight());
                    mPopupWindow.showAsDropDown(mAnchor, xoff, yoff);
                } else {
                    mPopupWindow.showAsDropDown(mAnchor, xOffset, yOffset);
                }
            }
        }
    }

    /**
     * 关闭弹出菜单
     */
    public void dismiss() {
        if (mPopupWindow != null)
            mPopupWindow.dismiss();
    }
    /***************************内部方法*******************************/
    /**
     * 创建弹窗
     */
    private void createPopup() {
        mPopupWindow = new PopupWindow();
        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
        mPopupWindow.setBackgroundDrawable(dw);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPopupWindow.setElevation(0f);
        }
        mPopupWindow.setTouchable(true);
        CardView cardView = new CardView(mContext);
        cardView.setRadius(10f);
        cardView.setCardElevation(5);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        params.setMargins(5, 5, 5, 5);
        cardView.setLayoutParams(params);
        mContainerLayout = new LinearLayout(mContext);

        mContainerLayout.setLayoutParams(params);
        //cardView.setElevation(dp2px(mContext, 5));
        mContainerLayout.setBackgroundColor(Color.WHITE);
        mContainerLayout.setOrientation(LinearLayout.VERTICAL);
        //mContainerLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        mContainerLayout.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        //布局位于状态栏下⽅
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
                //全屏
                //View.SYSTEM_UI_FLAG_FULLSCREEN ;//|
                ////隐藏导航栏
                //View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                //View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                uiOptions |= 0x00001000;
                mContainerLayout.setSystemUiVisibility(uiOptions);
            }
        });
        cardView.addView(mContainerLayout);
        mPopupWindow.setContentView(cardView);
    }

    /**
     * 创建菜单项
     *
     * @param index    位置
     * @param menuItem 菜单项目
     */
    private void createMenuItemView(int index, MenuItem menuItem) {
        /*****************创建菜单项视图********************/
        LinearLayout menuItemView = new LinearLayout(mContext);
        menuItemView.setOrientation(LinearLayout.HORIZONTAL);
        if (menuItem.isSplitLine()) {
            View view = new View(mContext);
            view.setBackgroundColor(0x60888888);   //默认分割线颜色
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, 2, 0);
            layoutParams.setMargins((int) offset * 2, (int) offset, (int) offset * 2, (int) offset);
            view.setLayoutParams(layoutParams);
            menuItemView.setTag(menuItem);  //将菜单项目内容包含在tag
            menuItemView.addView(view);
            if (menuItem.isVisibility()) {
                menuItemView.setVisibility(View.VISIBLE);
                menuHeight += 2 + offset * 2;
            } else {
                menuItemView.setVisibility(View.GONE);
            }
            if (index >= 0) {
                mContainerLayout.addView(menuItemView, index);
            } else {
                mContainerLayout.addView(menuItemView);
            }
        } else {
            menuItemView.setClickable(true);
            menuItemView.setFocusable(true);
            menuItemView.setBackgroundResource(android.R.drawable.menuitem_background);
            menuItemView.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, 0));
            menuItemView.setPadding(0, (int) offset, 0, (int) offset);
            menuItemView.setGravity(Gravity.CENTER_VERTICAL); //子视图垂直居中对齐
            /****************创建并设置菜单图标*****************/
            ImageView iconView = null;
            LinearLayout.LayoutParams iconParams = null;
            if (menuItem.getItemIcon() != null) {
                iconView = new ImageView(mContext); //创建菜单项图标
                iconView.setImageDrawable(menuItem.getItemIcon());
                iconParams = new LinearLayout.LayoutParams((int) iconSize, (int) iconSize, 0);
                iconParams.leftMargin = (int) offset;
                iconParams.rightMargin = (int) offset / 2;
                iconView.setLayoutParams(iconParams);
            }
            /*******************创建并设置菜单标签***********************/
            TextView titleView = new TextView(mContext);  //创建菜单项标签
            titleView.setText(menuItem.getItemTitle());
            titleView.setGravity(Gravity.CENTER_VERTICAL);  //标签垂直居中对齐
            titleView.setSingleLine(true);    //单行文本
            titleView.setEllipsize(TextUtils.TruncateAt.END);  //结尾省略
            LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(MATCH_PARENT, (int) itemHeight, 1);
            titleParams.leftMargin = (int) (iconView != null ? (offset / 2) : offset);
            titleParams.rightMargin = (int) offset;
            titleView.setLayoutParams(titleParams);
            TextPaint textPaint = titleView.getPaint();
            float textWidth = textPaint.measureText(menuItem.getItemTitle()); //得到文本宽度
            //因为文本框单行间隔3dp所有这里将文本高度保留3dp
            //float textHeight=textPaint.descent()-textPaint.ascent()+dp2px(mContext,3);
            //Log.e(TAG, "标题宽度:" + textWidth);
            /****************占位视图*******************/
            Space space = new Space(mContext);
            space.setLayoutParams(new LinearLayout.LayoutParams((int) this.spaceWidth, 0));
            /****************将图标和标签添加到视图***************/
            if (iconView != null)
                menuItemView.addView(iconView);
            menuItemView.addView(titleView);
            menuItemView.addView(space);
            /******************switchItem****************/
            int switchWidth = 0;
            Switch mSwidth = null;
            if (menuItem.isSwitchItem()) {
                mSwidth = new Switch(mContext);
                int w = (int) dp2px(mContext, 50);
                LinearLayout.LayoutParams switchParams = new LinearLayout.LayoutParams(w, (int) itemHeight, 0);
                switchParams.rightMargin = (int) offset;
                mSwidth.setLayoutParams(switchParams);
                mSwidth.setChecked(menuItem.isSwitchChecked());   //设置选中状态
                switchWidth = switchParams.rightMargin + w;
                menuItemView.addView(mSwidth);
                // mSwidth.setChecked();
            }
            menuItemView.setSelected(menuItem.isSelected());  //选中状态
            /*********将菜单项添加到菜单视图***********/
            menuItemView.setTag(menuItem);  //将菜单项目内容包含在tag
            menuItemView.setEnabled(menuItem.isEnabled());
            if (menuItem.isVisibility()) {
                menuItemView.setVisibility(View.VISIBLE);
                //当前所需宽度=文本宽度+文本视图左右保留位置+图标宽度+图标左右保留位置+占位视图宽度+切换开关宽度
                float tempWidth = textWidth + titleParams.leftMargin + titleParams.rightMargin +
                        (iconParams != null ? iconSize + iconParams.leftMargin + iconParams.rightMargin : 0) +
                        spaceWidth + switchWidth;
                if (tempWidth > menuWidth) {
                    menuWidth = (int) tempWidth;
                }
                menuHeight += itemHeight + offset * 2;   //固定项目高度
            } else {
                menuItemView.setVisibility(View.GONE);
            }
            if (index < 0 || index >= mContainerLayout.getChildCount()) {
                mContainerLayout.addView(menuItemView);
            } else {
                mContainerLayout.addView(menuItemView, index);
            }
            if (mSwidth != null)
                mSwidth.setOnCheckedChangeListener(switchChangeListener);   //侦听侦听改变
            menuItemView.setOnClickListener(clickListener);    //设置菜单项视图单击事件
        }
    }

    /**
     * 指定关键字是否为菜单列表唯一关键字
     *
     * @param key 需要检测的关键字
     * @return true 唯一；false 重复
     */
    private boolean isMenuItemKeyOnly(String key) {
        for (int pos = 0; pos < mContainerLayout.getChildCount(); pos++) {
            MenuItem item = (MenuItem) mContainerLayout.getChildAt(pos).getTag();
            if (item != null) {
                if (item.getItemKey().equals(key)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 计算菜单项需要的宽度
     *
     * @param currentMenuItemView
     * @return
     */
    private float getMenuItemWidth(LinearLayout currentMenuItemView) {
        float textWidth = 0, imgWidth = 0, switchWidth = 0;
        for (int i = 0; i < currentMenuItemView.getChildCount(); i++) {
            View view = currentMenuItemView.getChildAt(i);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
            if (view instanceof TextView) {
                TextPaint textPaint = ((TextView) view).getPaint();
                textWidth = textPaint.measureText(((TextView) view).getText().toString()); //得到文本宽度
                textWidth += params.leftMargin + params.rightMargin;
            } else if (view instanceof ImageView) {
                imgWidth = iconSize + params.leftMargin + params.rightMargin;
            } else if (view instanceof Switch) {
                float w = dp2px(mContext, 50);
                switchWidth = w + params.rightMargin;
            }
        }
        return textWidth + imgWidth + this.spaceWidth + switchWidth;
    }

    /**
     * 重新计算并设置菜单宽度
     */
    private void setMenuWidth() {
        float mWidth = 0;
        for (int index = 0; index < mContainerLayout.getChildCount(); index++) {
            float tempWidth = getMenuItemWidth((LinearLayout) mContainerLayout.getChildAt(index));
            if (tempWidth > mWidth)
                mWidth = tempWidth;
        }
        menuWidth = mWidth;
    }

    private float sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return spValue * fontScale;
    }

    private float dp2px(Context context, float dpValue) {
        final float fontScale = context.getResources().getDisplayMetrics().density;
        return dpValue * fontScale;
    }

    /**
     * 定义菜单项改变事件侦听
     */
    private MenuItem.OnMenuItemChange menuItemChange = new MenuItem.OnMenuItemChange() {
        @Override
        public void onChange(MenuItem item, String attributeName, Object value) {
            for (int id = 0; id < mContainerLayout.getChildCount(); id++) {
                LinearLayout menuItemView = (LinearLayout) mContainerLayout.getChildAt(id);
                MenuItem menuItem = (MenuItem) menuItemView.getTag();
                if (item.equals(menuItem)) {
                    //Log.e(TAG,attributeName+"改变");
                    if (attributeName.equals("selected")) {
                        menuItemView.setSelected((boolean) value);
                    } else if (attributeName.equals("enabled")) {
                        boolean enabled = (boolean) value;
                        menuItemView.setEnabled(enabled);
                        if (!item.isSplitLine() && item.isSwitchItem()) {
                            if (menuItemView.getChildAt(menuItemView.getChildCount() - 1) instanceof Switch) {
                                menuItemView.getChildAt(menuItemView.getChildCount() - 1).setEnabled(enabled);
                            }
                        }
                    } else if (attributeName.equals("visibility")) {
                        boolean visibility = (boolean) value;
                        if (visibility) {
                            if (menuItemView.getVisibility() == View.GONE) {
                                if (item.isSplitLine()) {
                                    menuHeight -= 2 + offset * 2;
                                } else {
                                    menuHeight -= itemHeight + offset * 2;
                                }
                                menuItemView.setVisibility(View.VISIBLE);
                            }
                        } else {
                            if (menuItemView.getVisibility() != View.GONE) {
                                if (item.isSplitLine()) {
                                    menuHeight += 2 + offset * 2;
                                } else {
                                    menuHeight += itemHeight + offset * 2;
                                }
                                menuItemView.setVisibility(View.GONE);
                            }
                        }
                    } else if (attributeName.equals("switchItem")) {
                        boolean val = (boolean) value;
                        if (val) {
                            if (!(menuItemView.getChildAt(menuItemView.getChildCount() - 1) instanceof Switch)) {
                                Switch mSwidth = new Switch(mContext);
                                mSwidth.setChecked(item.isSwitchChecked());
                                menuItemView.addView(mSwidth);
                                mSwidth.setOnCheckedChangeListener(switchChangeListener);  //设置侦听
                            }
                            //在项目中增加新控件时直接比对得到最大宽度即可
                            float itemWidth = getMenuItemWidth(menuItemView);
                            if (itemWidth > menuWidth)
                                menuWidth = itemWidth;
                        } else {
                            if ((menuItemView.getChildAt(menuItemView.getChildCount() - 1) instanceof Switch)) {
                                menuItemView.removeViewAt(menuItemView.getChildCount() - 1);
                                setMenuWidth();    //由于是减少视图，所以需要重新计算所有菜单项宽度来得到新宽度
                            }
                        }
                    } else if (attributeName.equals("itemIcon")) {
                        Drawable icon = (Drawable) value;
                        if (icon == null) {
                            if (menuItemView.getChildAt(0) instanceof ImageView) {
                                menuItemView.removeViewAt(0);
                                setMenuWidth();    //由于是减少视图，所以需要重新计算所有菜单项宽度来得到新宽度
                            }
                        } else {
                            ImageView iconView;
                            if (menuItemView.getChildAt(0) instanceof ImageView) {
                                iconView = (ImageView) menuItemView.getChildAt(0);
                                if (!iconView.getDrawable().equals(icon)) {
                                    iconView.setImageDrawable(icon);
                                }
                            } else {
                                iconView = new ImageView(mContext);
                                iconView.setImageDrawable(menuItem.getItemIcon());
                                LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams((int) iconSize, (int) iconSize, 0);
                                iconParams.leftMargin = (int) offset;
                                iconParams.rightMargin = (int) offset / 2;
                                iconView.setLayoutParams(iconParams);
                                menuItemView.addView(iconView, 0);
                                //在项目中增加新控件时直接比对得到最大宽度即可
                                float itemWidth = getMenuItemWidth(menuItemView);
                                if (itemWidth > menuWidth)
                                    menuWidth = itemWidth;
                            }
                        }
                    } else if (attributeName.equals("itemTitle")) {
                        String title = (String) value;
                        if (title == null)
                            title = "";
                        for (int i = 0; i < menuItemView.getChildCount(); i++) {
                            if (menuItemView.getChildAt(i) instanceof TextView) {
                                TextView textView = (TextView) menuItemView.getChildAt(i);
                                if (!textView.getText().equals(title)) {
                                    textView.setText(title);
                                    setMenuWidth();    //由于标签内容长度不确定，所以直接重新计算整个菜单宽度
                                    return;
                                }
                            }
                        }
                    }
                    return;
                }
            }
        }
    };
    /**
     * Switch状态改变事件接口定义
     */
    private OnCheckedChangeListener switchChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (switchCheckedChangeListener != null) {
                LinearLayout menuItemView = (LinearLayout) buttonView.getParent();
                if (menuItemView != null) {
                    MenuItem item = (MenuItem) menuItemView.getTag();
                    switchCheckedChangeListener.onCheckedChanged(item, buttonView, isChecked);
                }
            }
            //Log.e(TAG,((MenuItem) ((LinearLayout)buttonView.getParent()).getTag()).getItemKey()+"项菜单复选按钮改变");
        }
    };
    private final View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mPopupWindow.dismiss();
            if (menuItemClickListener != null) {
                menuItemClickListener.onMenuItemClick((MenuItem) v.getTag(), v);
                menuItemClickListener.onMenuItemClick((MenuItem) v.getTag(), v, position);
            }
            // Log.e(TAG,"按下"+((MenuItem) v.getTag()).getItemKey()+"项菜单");
        }
    };
}


