**JSON Normaliser**

The purpose of this utility is to cleanse the JSON that has a lot of duplicated fields and values in 
multiple sections and normalize them into a single field without writing any code.

The main motivation behind this utility is to make the JSON nice and compact to be indexed efficiently into
Elasticsearch without having to make your index consume too much of space (with duplicated fields/values).

The best way to explain this is via a few examples:

***Example 1***

****Input JSON****
```
{
  "firstName": "John",
  "lastName": "Smith",
  "fullName": "John Smith",
  "age": 31,
  "city": "New York"
}
```

****Settings JSON****
```
{
  "documentType": "events",
  "fieldSettings": [
    {
      "primaryField": "fullName",
      "secondaryFields": [
        "firstName",
        "lastName"
      ]
    }
  ]
}
```

As you see above, my intent is to combine the firstName and lastName fields, dedupe them and munge them into the fullName
field.

The output looks like this:
```
{
  "fullName": "John Smith",
  "age": 31,
  "city": "New York"
}
```



