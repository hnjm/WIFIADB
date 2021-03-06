package adb.wifi.woaiwhz.component.base;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Queue;

/**
 * Created by huazhou.whz on 2016/10/17.
 */
public abstract class BaseAdapter<T extends BaseAdapter.ViewHolder> {
    private JPanel mContainer;
    private final Map<Integer,Queue<T>> mComponentPool;
    protected final List<T> mHolderList;

    {
        mComponentPool = new HashMap<>();
        mHolderList = new ArrayList<>();
    }

    public void attach(@NotNull JPanel container){
        if (mContainer != null){
            mContainer.removeAll();
            mContainer.updateUI();
        }

        mContainer = container;
        mComponentPool.clear();
    }

    public void notifyDataSetChange() {
        if (mContainer == null) {
            return;
        }

        mContainer.removeAll();
        mHolderList.clear();

        final int count = getItemCount();

        if (count <= 0) {
            mContainer.updateUI();
            return;
        }

        final Map<Integer, Queue<T>> tmpMap = new HashMap<>();

        for (int i = 0; i < count; ++i) {
            final int viewType = getItemViewType(i);
            Queue<T> queue = mComponentPool.get(viewType);

            if (queue == null) {
                queue = new LinkedList<>();
                mComponentPool.put(viewType, queue);
            }

            Queue<T> tmpQueue = tmpMap.get(viewType);

            if (tmpQueue == null) {
                tmpQueue = new LinkedList<>();
                tmpMap.put(viewType, tmpQueue);
            }

            T viewHolder = queue.poll();

            if (viewHolder == null) {
                viewHolder = onCreateViewHolder(viewType);
            }

            if (viewHolder == null) {
                throw new NullPointerException();
            }

            tmpQueue.offer(viewHolder);
            onBindViewHolder(viewHolder, i);
            mHolderList.add(viewHolder);

            final Component component = viewHolder.getRoot();

            if(component == null){
                throw new NullPointerException();
            }

            mContainer.add(component);
        }

        final Set<Integer> tmpKeys = tmpMap.keySet();
        for (final Integer key : tmpKeys){
            final Queue<T> queue = mComponentPool.get(key);
            final Queue<T> tmpQueue = tmpMap.get(key);

            if(queue == null || tmpQueue == null){
                throw new IllegalStateException();
            }

            queue.addAll(tmpQueue);
        }

        mContainer.updateUI();
    }

    protected abstract int getItemCount();

    protected abstract int getItemViewType(int position);

    protected abstract T onCreateViewHolder(int viewType);

    protected abstract void onBindViewHolder(T holder,int position);

    public static abstract class ViewHolder{
        protected abstract Component getRoot();
    }
}
