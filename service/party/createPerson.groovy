import org.moqui.context.ExecutionContext
import org.moqui.entity.EntityValue

ExecutionContext ec = context.ec

// 1. Validate that partyId, firstName, and lastName are provided
// Return an error if any required parameter is missing
if (!context.partyId) {
    ec.message.addError("Parameter partyId is required.")
}
if (!context.firstName) {
    ec.message.addError("Parameter firstName is required.")
}
if (!context.lastName) {
    ec.message.addError("Parameter lastName is required.")
}

if (ec.message.hasError()) return

String partyId = context.partyId
String firstName = context.firstName
String lastName = context.lastName

// 2. Verify that a Party record exists for the given partyId
EntityValue party = ec.entity.find("party.Party").condition("partyId", partyId).one()
if (party == null) {
    ec.message.addError("Party record with ID [${partyId}] does not exist.")
    return
}

// 3. Ensure the Person is created only if the Party exists
// Accept and process any additional parameters
Map personMap = [partyId: partyId, firstName: firstName, lastName: lastName]

// Fetch the schema definition of party.Person to identify its valid fields
def personDefinition = ec.entity.getEntityDefinition("party.Person")
for (String fieldName in personDefinition.getFieldNames(true, true, false)) {
    if (context.containsKey(fieldName) && context.get(fieldName) != null) {
        personMap.put(fieldName, context.get(fieldName))
    }
}

// Create the Person record
EntityValue newPerson = ec.entity.makeValue("party.Person")
newPerson.setAll(personMap)
newPerson.create()

// 4. Return the response string
context.response = "Person ${firstName} ${lastName} created successfully!"
