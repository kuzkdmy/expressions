### Expression parsers

#### General idea

This is all about objects and their fields

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

#### TODO
- use tofu logging, they implement all as it really should be done with Fiber context