package lt.vilnius.tvarkau.widgets

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

/**
 * @author Martynas Jurkus
 */
class EndlessScrollListener(
        val load: () -> Unit,
        val loadItemPerPage: Int = 20
) : RecyclerView.OnScrollListener() {

    var isLoading = false
    var isEnabled = true

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val visibleItemCount = recyclerView.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
        onScrolled(firstVisibleItem, visibleItemCount, totalItemCount)
    }

    private fun onScrolled(firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
        if (!isLoading && isEnabled && (totalItemCount - firstVisibleItem - visibleItemCount) < loadItemPerPage / 2) {
            load()
        }
    }
}