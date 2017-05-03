package sbingo.likecloudmusic.ui.adapter.RvAdapter;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Author: Sbingo
 * Date:   2016/12/11
 */

public abstract class BaseRvAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected List<T> mList = new ArrayList<>();
    /**
     * 保存选中的项，可设置为多选
     */
    protected Map<Integer, Boolean> checkedMap = new HashMap<>();
    /**
     * 默认为单选模式
     */
    protected boolean isSingleChoice = true;

    private CompositeSubscription mCompositeSubscription;

    @Override
    public int getItemCount() {
        if (mList == null) {
            return 0;
        }
        return mList.size();
    }

    /**
     * 清除所有选中状态
     */
    public void clearCheckedItems() {
        for (int i = 0; i < mList.size(); i++) {
            checkedMap.put(i, false);
        }
        notifyDataSetChanged();
    }

    /**
     * 设置选中状态的统一入口
     * 有单选和多选的区别
     * 默认为单选
     *
     * @param position
     */
    protected void itemChecked(int position) {
        if (isSingleChoice) {
            setChecked(position);
        } else {
            switchCheckedStatus(position);
        }
    }

    /**
     * 单选模式下选中
     *
     * @param position
     */
    private void setChecked(int position) {
        for (int i = 0; i < mList.size(); i++) {
            if (position == i) {
                checkedMap.put(i, true);
            } else {
                checkedMap.put(i, false);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 多选模式下切换选中
     *
     * @param position
     */
    private void switchCheckedStatus(int position) {
        if (checkedMap.get(position)) {
            checkedMap.put(position, false);
        } else {
            checkedMap.put(position, true);
        }
        notifyItemChanged(position);
    }

    /**
     * 获取选中的项
     *
     * @return
     */
    public List<Integer> getCheckedPositions() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < checkedMap.size(); i++) {
            if (checkedMap.get(i)) {
                list.add(i);
            }
        }
        return list;
    }

    public void unSubscribe() {
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
    }

    protected void addSubscribe(Subscription subscription) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscription);
    }

    public List<T> getList() {
        return mList;
    }

    public T getLast() {
        return mList.get(getItemCount() - 1);
    }

    public T getItem(int position) {
        if (position >= getItemCount()) {
            throw new RuntimeException("-----查询item时数组越界！-----");
        }
        return mList.get(position);
    }

    public void setList(List<T> data) {
        mList.clear();
        mList.addAll(data);
        notifyDataSetChanged();
    }

    public void addMore(List<T> data) {
        int startPosition = mList.size();
        mList.addAll(data);
        notifyItemRangeInserted(startPosition, data.size());
    }

    public void addItem(T data) {
        int startPosition = mList.size();
        mList.add(data);
        notifyItemInserted(startPosition);
    }

    public void deleteItem(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
    }

    public void deleteLastItems(int count) {
        int originalCount = getItemCount();
        for (int i = originalCount - 1; i >= (originalCount - count); i--) {
            mList.remove(i);
        }
        notifyItemRangeRemoved(originalCount - count, originalCount - 1);
    }

    public void changeItem(int position, T item) {
        mList.set(position, item);
        notifyItemChanged(position);
    }

    public void clear() {
        mList.clear();
        notifyDataSetChanged();
    }

}
