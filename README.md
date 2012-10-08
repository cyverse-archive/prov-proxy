# prov-proxy

Adapter for interacting with the provenance API.

## Getting an Object's UUID

HTTP Method: GET
URL Path:    /0.1/object/<object-id>

Returns a 200 status on success, along with a JSON body that looks like the following:

    {
        "status" : "success",
        "action : "get-object-uuid",
        "uuid" : "<object-uuid>" 
    }

Returns a 404 if object-id isn't registered yet.

Returns a 500 error if an error occurs. The body of the response will look like this:

    {
        "status" : "failure",
        "action" : "get-object-uuiid",
        "error_code" : "<error_code>"
        ...
    } 

Additional keys are added depending on the error. A list of possible error_code values will be provided later.

## Registering an Object

HTTP Method: PUT
URL Path: /0.1/object
Body Format: JSON

Here's what the body of the request should look like:

    {
        "id" : "<unique identifier>",
        "name" : "<object name>",
        "desc" : "<object description>"
    }

The "id" field is an application specific unique identifier. It's not necessarily a UUID. For instance, in iRODS a user's "id" could be "username#zone" while their "name" is their full name. "desc" is a free-form text field.

Returns a 200 status code on success, along with a JSON body that looks like this:

    {
        "status" : "success",
        "action" : "add-object",
        "uuid" : "<object uuid>",
        "id" : "<id passed in the request>",
        "name" : "<name passed in the request>",
        "desc" : "<desc passed in the request>"
    }

The only interesting field is the "uuid" field, which contains the UUID associated with the object in the Provenance server.

Returns a 500 status code on failure, along with a JSON body that looks like this:

    {
        "status" : "failure",
        "action" : "add-object",
        "error_code" : "<error_code>",
        "id" : "<id passed in the request>",   
        "name" : "<name passed in the request>",
        "desc" : "<desc passed in the request>",
        ...
    }

Additional keys are added depending on the error. A list of possible error_code values will be provided later.

## Logging Provenance Information

HTTP Method: PUT
URL Path: /0.1/log
Body Format: JSON

The request body should look something like this:

    {
        "object-id" : "<app-specific unique identifier>",
        "user" : "<username of the user making the call>",
        "service" : "<name of the service generating the call>",
        "event" : "<name of the event being logged>",
        "category" : "<name of the category>",
        "proxy-user" : "<username of the proxy user>",
        "data" : "<arbitrary data associated with the call>"
    }

The "proxy-user" and "data" fields are optional. The rest are required. Multipart requests aren't supported.

Returns a 200 status code on success, along with a JSON body that looks something like this:

    {
        "status" : "success",
        "action" : "log",
        "object-id" : "<app-specific unique identifier>",
        "user" : "<username of the user making the call>",
        "service" : "<name of the service generating the call>",
        "event" : "<name of the event being logged>",
        "category" : "<name of the category>",
        "proxy-user" : "<username of the proxy user>"
    }

The "data" field is omitted from all response bodies (successful or otherwise) to ensure that the body size doesn't get too large. 

Returns a 500 status code on failure, along with a JSON body that looks something like the following:

    {
        "status" : "failure",
        "action" : "log",
        "error_code" : "<error_code>",
        "object-id" : "<app-specific unique identifier>",
        "user" : "<username of the user making the call>",
        "service" : "<name of the service generating the call>",
        "event" : "<name of the event being logged>",
        "category" : "<name of the category>",
        "proxy-user" : "<username of the proxy user>",
        ...
    }

Additional fields will be added depending on the error being reported. A list of potential error_codes will be added later.

