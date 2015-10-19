package ru.loftschool.loftblogmoneytracker.adapters;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;

import java.util.ArrayList;
import java.util.List;

import ru.loftschool.loftblogmoneytracker.utils.SparseBooleanArrayParcelable;

public abstract class SelectableAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private static final String TAG = SelectableAdapter.class.getSimpleName();
    private static final String BUNDLE_TAG = "SelectedItems";

    private SparseBooleanArray selectedItems;

    public SelectableAdapter() {
        selectedItems = new SparseBooleanArray();
    }

    public SparseBooleanArray getSparseBooleanSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(SparseBooleanArray selectedItems) {
        this.selectedItems = selectedItems;
    }

    public boolean isSelected(int position) {
        return getSelectedItems().contains(position);
    }

    public void toggleSelection(int position) {
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position);
        } else {
            selectedItems.put(position, true);

        }
        notifyItemChanged(position);
    }

    public void clearSelection() {
        List<Integer> selection = getSelectedItems();
        selectedItems.clear();
        for (Integer i : selection) {
            notifyItemChanged(i);
        }
    }

    public int getSelectedItemsCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public void onSaveInstanceState(Bundle outState) {
        SparseBooleanArrayParcelable parcelableSelectedItems = new SparseBooleanArrayParcelable(selectedItems);
        outState.putParcelable(BUNDLE_TAG, parcelableSelectedItems);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        SparseBooleanArrayParcelable parcelableSelectedItems = savedInstanceState.getParcelable(BUNDLE_TAG);
        if (parcelableSelectedItems != null) {
            selectedItems = parcelableSelectedItems;
        }
    }
}
