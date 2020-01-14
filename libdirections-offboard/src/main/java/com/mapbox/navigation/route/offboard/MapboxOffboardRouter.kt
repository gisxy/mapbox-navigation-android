package com.mapbox.navigation.route.offboard

import android.content.Context
import com.mapbox.annotation.navigation.module.MapboxNavigationModule
import com.mapbox.annotation.navigation.module.MapboxNavigationModuleType
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.navigation.base.route.Router
import com.mapbox.navigation.base.route.model.RouteOptionsNavigation
import com.mapbox.navigation.route.offboard.extension.mapToRoute
import com.mapbox.navigation.route.offboard.router.NavigationOffboardRoute
import com.mapbox.navigation.utils.exceptions.NavigationException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@MapboxNavigationModule(MapboxNavigationModuleType.OffboardRouter, skipConfiguration = true)
class MapboxOffboardRouter(private val accessToken: String, private val context: Context) : Router {

    companion object {
        const val ERROR_FETCHING_ROUTE = "Error fetching route"
    }

    private var navigationRoute: NavigationOffboardRoute? = null
    private var callback: Router.Callback? = null

    override fun getRoute(
        routeOptions: RouteOptionsNavigation,
        callback: Router.Callback
    ) {
        cancel()
        this.callback = callback
        navigationRoute = RouteBuilderProvider.getBuilder(accessToken, context).routeOptions(routeOptions).build()
        navigationRoute?.getRoute(object : Callback<DirectionsResponse> {

            override fun onResponse(
                call: Call<DirectionsResponse>,
                response: Response<DirectionsResponse>
            ) {
                val routes = response.body()?.routes()
                if (response.isSuccessful && !routes.isNullOrEmpty()) {
                    notifyCallback { routes.map { it.mapToRoute() } }
                } else {
                    notifyCallback { it.onFailure(NavigationException(ERROR_FETCHING_ROUTE)) }
                }
            }

            override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                notifyCallback { it.onFailure(t) }
            }
        })
    }

    override fun cancel() {
        navigationRoute?.cancelCall()
        notifyCallback { it.onCanceled() }
    }

    private fun notifyCallback(func: (Router.Callback) -> Unit) {
        navigationRoute = null
        callback?.let {
            func(it)
            callback = null
        }
    }
}
