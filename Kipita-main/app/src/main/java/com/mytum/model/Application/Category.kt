package com.mytum.model.Application

import com.mytum.interfaces.ParentListItem


data class Category(val name: String, val movieList: List<CategoryList>) : ParentListItem {
    override fun getChildItemList(): List<*> = movieList
    override fun isInitiallyExpanded(): Boolean = false
}