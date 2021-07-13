package hr.algebra.infiniguide


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import hr.algebra.infiniguide.model.Route
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.route.view.*

class ItemAdapter(val routes: List<Route>) : RecyclerView.Adapter<ItemAdapter.RouteViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        return RouteViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.route, parent, false)
        )

    }

    override fun getItemCount() = routes.size

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        val route = routes[position]

        holder.view.routeName.text = route.Name
        holder.view.routeAbout.text = route.About
        Picasso.get()
            .load(route.PicturePath)
            .error(R.drawable.defaultgoogle)
            .transform(RoundedCornersTransformation(80, 8))
            .into(holder.view.routeImage)

        holder.route = route
    }

    class RouteViewHolder(val view: View, var route: Route? = null) : RecyclerView.ViewHolder(view){

        companion object{
            val ROUTE_ID_KEY = "ROUTE_ID"
            val ROUTE_NAME = "ROUTE_NAME"
            val ROUTE_ABOUT = "ROUTE_ABOUT"
        }

        init {
            view.setOnClickListener{
                val intent = Intent(view.context, ItemActivity::class.java)
                intent.putExtra(ROUTE_ID_KEY, route?.IDRoute.toString())
                intent.putExtra(ROUTE_NAME, route?.Name)
                intent.putExtra(ROUTE_ABOUT, route?.About)
                view.context.startActivity(intent)
            }
        }
    }
}
