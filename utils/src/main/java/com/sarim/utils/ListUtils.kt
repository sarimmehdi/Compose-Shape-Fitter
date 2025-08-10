package com.sarim.utils

fun <T> shuffleListExceptOne(
    list: MutableList<T>,
    fixedElementIndex: Int,
): List<T> {
    if (fixedElementIndex < 0 || fixedElementIndex >= list.size) {
        throw IndexOutOfBoundsException("Invalid index for fixed element")
    }

    val fixedElement = list.removeAt(fixedElementIndex)
    list.shuffle()
    list.add(fixedElementIndex, fixedElement)

    return list
}
