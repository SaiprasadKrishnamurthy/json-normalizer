**JSON Normalizer**

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

****Output JSON****
```
{
  "fullName": "John Smith",
  "age": 31,
  "city": "New York"
}
```

***Example 2***

****Input JSON****
```
{
  "identity": null,
  "firstName": "John",
  "lastName": "John",
  "fullName": "John John",
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
      "primaryField": "identity",
      "secondaryFields": [
        "firstName",
        "lastName",
        "age",
        "city"
      ],
      "unwantedFields": [
        "fullName"
      ],
      "valuesDelimiter": ","
    }
  ]
}
```

As you see above, my intent is to combine the firstName, lastName, age, city fields, dedupe them and munge them into the identity
field. I also declared that I do not want fullName field as they are already taken care of.

****Output JSON****
```
{
  "identity": "31,John,New York"
}
```

***Example 3 (using arrays)***

****Input JSON****
```
{
  "identity": null,
  "firstName": "John",
  "lastName": "Smith",
  "fullName": "John Smith",
  "age": 31,
  "phoneNumbers": ["111", "234", "111"],
  "city": "New York"
}
```

****Settings JSON****
```
{
  "documentType": "events",
  "fieldSettings": [
    {
      "primaryField": "identity",
      "secondaryFields": [
        "firstName",
        "lastName",
        "age",
        "city",
        "phoneNumbers"
      ],
      "unwantedFields": [
        "fullName"
      ],
      "valuesDelimiter": ","
    }
  ]
}
```

As you see above, my intent is to combine the firstName, lastName, age, city, phone fields, dedupe them and munge them into the identity
field. The array values are automatically handled.

****Output JSON****
```
{
  "identity": "111,234,31,John,New York,Smith"
}
```

***Example 4 (using arrays)***

****Input JSON****
```
{
  "names": null,
  "coordinates": null,
  "firstName": "John",
  "lastName": "Smith",
  "fullName": "John Smith",
  "age": 31,
  "phoneNumbers": [
    "111",
    "234",
    "111"
  ],
  "city": "New York",
  "contacts": {
    "homePhone": [
      "111",
      "000"
    ],
    "officePhone": [
      "111",
      "100"
    ]
  }
}
```

****Settings JSON****
```
{
  "documentType": "events",
  "fieldSettings": [
    {
      "primaryField": "names",
      "secondaryFields": [
        "firstName",
        "lastName"
      ],
      "unwantedFields": [
        "fullName"
      ],
      "valuesDelimiter": ","
    },
    {
      "primaryField": "phoneNumbers",
      "secondaryFields": [
        "contacts.homePhone",
        "contacts.officePhone"
      ],
      "valuesDelimiter": ",",
      "unwantedFields": [
        "contacts"
      ]
    }
  ]
}
```

****Output JSON****
```
{
  "names": "John,Smith",
  "coordinates": null,
  "age": 31,
  "phoneNumbers": [
    "000",
    "100",
    "111",
    "234"
  ],
  "city": "New York"
}
```


