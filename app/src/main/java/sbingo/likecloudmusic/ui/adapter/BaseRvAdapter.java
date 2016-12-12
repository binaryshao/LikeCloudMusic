package sbingo.likecloudmusic.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Sbingo
 * Date:   2016/12/11
 */

public class BaseRvAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected List<T> mList = new ArrayList<>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        if (mList == null) {
            return 0;
        }
        return mList.size();
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
}
