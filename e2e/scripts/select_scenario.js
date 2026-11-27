// Selects a scenario on the fake backend. Inputs (Maestro env): BACKEND_URL, SCENARIO.
var response = http.post(BACKEND_URL + '/__scenario/' + SCENARIO, { body: '' })
if (response.status != 200) {
  throw new Error('Could not select scenario "' + SCENARIO + '": HTTP ' + response.status + ' ' + response.body)
}
