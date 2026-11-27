package cat.xlagunas.e2e.backend

import cat.xlagunas.test.utils.http.RouteTable
import cat.xlagunas.test.utils.http.httpError
import cat.xlagunas.test.utils.http.ok

object Scenarios {
    val all: Map<String, (RouteTable) -> Unit> =
        mapOf(
            "logged_in_contacts" to { routes ->
                routes.authenticatedUser()
                routes.contactList("contact/contact_list.json")
                routes.contactActions()
            },
            "contacts_empty" to { routes ->
                routes.authenticatedUser()
                routes.contactList("contact/contact_list_empty.json")
            },
            "register" to { routes ->
                routes.respondTo("PUT", "/user/", ok())
                routes.contactList("contact/contact_list_empty.json")
            },
            "login_rejected" to { routes ->
                routes.respondTo("POST", "/auth/", httpError(401))
                routes.contactList("contact/contact_list_empty.json")
            },
            "empty" to { },
        )

    private fun RouteTable.authenticatedUser() {
        respondTo("POST", "/auth/", ok("user/auth_token.json"))
        respondTo("GET", "/user/", ok("user/user_anna.json"))
        respondTo("PUT", "/token/", ok())
    }

    private fun RouteTable.contactList(fixture: String) {
        respondTo("GET", "/contact/list", ok(fixture))
    }

    private fun RouteTable.contactActions() {
        respondTo("GET", "/contact/search/.*", ok("contact/search_jordi.json"))
        respondTo("PUT", "/contact/\\d+", ok())
        respondTo("POST", "/contact/\\d+/accept", ok())
        respondTo("POST", "/contact/\\d+/reject", ok())
        respondTo("PUT", "/call/", ok("call/call_created.json"))
    }
}
