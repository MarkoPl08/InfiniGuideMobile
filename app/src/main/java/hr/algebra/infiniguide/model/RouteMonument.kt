package hr.algebra.infiniguide.model

data class RouteMonument(
        val IDRouteMonument: Int,
        val RouteID: Int,
        val OrderNumber: Int,
        val MonumentID: Int,
        val Monument: Monument,
        val Route: Route
)