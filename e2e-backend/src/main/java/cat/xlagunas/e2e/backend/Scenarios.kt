package cat.xlagunas.e2e.backend

import cat.xlagunas.test.utils.http.RouteTable
import cat.xlagunas.test.utils.http.ok

object Scenarios {
    val all: Map<String, (RouteTable) -> Unit> =
        mapOf(
            "logged_in_contacts" to { routes ->
                routes.respondTo("POST", "/auth/", ok("user/auth_token.json"))
                routes.respondTo("GET", "/user/", ok("user/user_anna.json"))
                routes.respondTo("PUT", "/token/", ok())
                routes.respondTo("GET", "/contact/list", ok("contact/contact_list.json"))
            },
            "empty" to { },
        )
}
