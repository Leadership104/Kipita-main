package com.mytum.imagepickerlibrary.ui.adapter

import androidx.databinding.ViewDataBinding


import com.mytum.imagepickerlibrary.listener.ItemClickListener
import com.mytum.imagepickerlibrary.model.Folder
import com.mytum.R
import com.mytum.databinding.ListItemFolderBinding

/**
 * FolderAdapter class to display folder items.
 */
internal class FolderAdapter(listener: ItemClickListener<Folder>) : BaseAdapter<Folder>(listener) {
    override fun getLayoutId(): Int = R.layout.list_item_folder

    override fun setDataForListItemWithPosition(
        binding: ViewDataBinding,
        data: Folder,
        adapterPosition: Int
    ) {
        super.setDataForListItemWithPosition(binding, data, adapterPosition)
        (binding as ListItemFolderBinding).folder = data
    }
}