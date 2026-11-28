// Asserts the fake backend received a request. Inputs (Maestro env): BACKEND_URL, METHOD, PATH,
// optional BODY_CONTAINS (substring the request body must contain).
var response = http.get(BACKEND_URL + '/__requests')
if (response.status != 200) {
  throw new Error('Could not read the request log: HTTP ' + response.status + ' ' + response.body)
}
var log = JSON.parse(response.body)
var found = log.some(function (entry) {
  if (entry.method != METHOD || entry.path != PATH) return false
  if (typeof BODY_CONTAINS === 'undefined' || BODY_CONTAINS === '') return true
  return (entry.body || '').indexOf(BODY_CONTAINS) !== -1
})
if (!found) {
  throw new Error(
    'Expected request ' + METHOD + ' ' + PATH +
      (typeof BODY_CONTAINS !== 'undefined' && BODY_CONTAINS !== '' ? ' with body containing "' + BODY_CONTAINS + '"' : '') +
      ' but the backend received: ' + response.body
  )
}
