package cat.xlagunas.viv.e2e

import cat.xlagunas.core.push.MessageType

/**
 * Builds the FCM data map for a `viv-e2e://push?type=...&params=...` link, or null when [type]
 * is not a known [MessageType]. The keys are the ones the push module's MessageConverter reads.
 */
fun pushData(
    type: String?,
    params: String?,
): Map<String, String>? {
    if (type == null || MessageType.values().none { it.name == type }) return null
    return buildMap {
        put("eventType", type)
        if (params != null) put("params", params)
    }
}
