### Expression parsers

#### General idea

This is all about objects and their fields

#### Mandatory tech todo
- split API and Internal Domain classes
- tapir swagger is great thing, but schema is not working correct with oneOfUsingField for recursive jumping via other schemas
- introduce configuration for objects and fields
- introduce validation for object and fields(response error should be properties projection based, so can highlight exact field and know error)
- as delete field really more complex thing we should support deprecation of fields and then can introduce logically deleted fields 
- persistence of internal domain should be linearized(hierarchy to multiple rows, this will helps a lot later)
- introduce project on selectors, so we can have (payment routing , ecommerce , account marketing ... expressions )
- predicate configuration on field level can ends with static or dynamic data, ui clients should understand this and don't call backend all time
- each object and field should have tags, ui clients can add more icons and styles based on tags
- support search of expressions based on predicates(only after linearize hierarchical api domain to multiple rows db domain)

#### Not mandatory tech TODO
- use tofu logging, they implement all as it really should be done with Fiber context

#### Domain

- expression (single expr to evaluate or expr that combine multiple expr to evaluate via and/or condition)
- expression rule (rule to evaluate on object level or on field level)
- field level rules (simple are on exact field types. object rule is for nested objects(hierarchically supported)
- predicate(depends on field type, usually something like equals/not equals/exists/between/...)

All information comes from server and UI is able to create dynamic expressions

#### How to create you own expressions
Field and Object predicates should allow build almost any hierarchical complexity expressions </br>
Setup of objects and their fields is required</br>
Frontend UI component should query available objects and allowed fields predicates 

#### Links
[Swagger docs](http://localhost:8293/docs/index.html?url=/docs/docs.yaml)
