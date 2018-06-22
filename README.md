**JSON Normalizer**

The purpose of this utility is to cleanse the JSON that has a lot of duplicated fields and values in 
multiple sections and normalize them into a single field without writing any code.

The main motivation behind this utility is to make the JSON nice and compact to be indexed efficiently into
Elasticsearch without having to make your index consume too much of space (with duplicated fields/values).

**To use this**
```
<dependency>
    <groupId>com.github.saiprasadkrishnamurthy</groupId>
    <artifactId>json-normalizer</artifactId>
    <version>1.1</version>
</dependency>

```
Call the below method:

```
DocumentNormalizer.normalize(final DocumentNormalizationSettings documentNormalizationSettings, final String originalJson)

```
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

***Example 4 (using multi-level nested document)***

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

***Example 5 (filtering a specific value using regex)***

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
    "111",
    "abc"
  ],
  "city": "New York",
  "contacts": {
    "homePhone": [
      "111",
      "000",
      "234a"
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
      "rejectValuesMatchingRegex": "\\D",
      "valuesDelimiter": ",",
      "unwantedFields": [
        "contacts"
      ]
    }
  ]
}
```
 I'm rejecting any non-numeric content in the phoneNumbers using the regex.
 
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

***Example 6 (A more complex example)***

****Input JSON****
```
{
  "names": null,
  "coordinates": null,
  "firstName": "John",
  "lastName": "Smith",
  "fullName": "John Smith",
  "age": 31,
  "phoneNumber": ["999"],
  "city": "New York",
  "orders": [
    {
      "orderId": "1",
      "total": 100,
      "items": [
        {
          "lineItemId": "1",
          "quantity": 1
        },
        {
          "lineItemId": "2",
          "quantity": 1
        }
      ],
      "phone": "111"
    },
    {
      "orderId": "2",
      "total": 100,
      "items": [
        {
          "lineItemId": "1",
          "quantity": 1
        },
        {
          "lineItemId": "2",
          "quantity": 1
        }
      ],
      "phone": "112"
    }
  ],
  "travelTickets": [
    {
      "ticketId": "1",
      "contactPhone": "111"
    }
  ]
}
```

****Settings JSON****
```
{
  "documentType": "events",
  "fieldSettings": [
    {
      "primaryField": "phoneNumber",
      "secondaryFields": [
        "orders.phone",
        "travelTickets.contactPhone"
      ],
      "unwantedFields": [
        "fullName"
      ],
      "valuesDelimiter": ","
    },
    {
      "primaryField": "phoneNumber",
      "secondaryFields": [
        "contacts.homePhone",
        "contacts.officePhone"
      ],
      "rejectValuesMatchingRegex": "\\D",
      "valuesDelimiter": ",",
      "unwantedFields": [
        "contacts"
      ]
    }
  ]
}
```
We have phone numbers at various depths in the input JSON. The settings would simply collect all the phone numbers
from various fields, dedupe them into a single "phoneNumber" field. The output JSON would look like below. 
 
****Output JSON****
```
{
  "names": null,
  "coordinates": null,
  "firstName": "John",
  "lastName": "Smith",
  "age": 31,
  "phoneNumber": [
    "111",
    "112",
    "999"
  ],
  "city": "New York",
  "orders": [
    {
      "orderId": "1",
      "total": 100,
      "items": [
        {
          "lineItemId": "1",
          "quantity": 1
        },
        {
          "lineItemId": "2",
          "quantity": 1
        }
      ]
    },
    {
      "orderId": "2",
      "total": 100,
      "items": [
        {
          "lineItemId": "1",
          "quantity": 1
        },
        {
          "lineItemId": "2",
          "quantity": 1
        }
      ]
    }
  ],
  "travelTickets": [
    {
      "ticketId": "1"
    }
  ]
}
```

***Example 7 (Dynamic fields or Virtual Fields)***

****Input JSON****
```
{
  "names": null,
  "coordinates": null,
  "firstName": "John",
  "lastName": "Smith",
  "fullName": "John Smith",
  "age": 31,
  "city": "New York",
  "contacts": {
    "homePhone": [
      "111",
      "000",
      "234a"
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
      "primaryField": "_phoneNumbers",
      "dynamicField": true,
      "secondaryFields": [
        "contacts.homePhone",
        "contacts.officePhone"
      ],
      "rejectValuesMatchingRegex": "\\D",
      "valuesDelimiter": " ",
      "unwantedFields": [
        "contacts"
      ]
    }
  ]
}
```
The "_phoneNumbers" field is a dynamic field and not present in the original input document. It's dynamically added.

****Output JSON****
```
{
  "names": "John,Smith",
  "coordinates": null,
  "age": 31,
  "city": "New York",
  "_phoneNumbers": "000 100 111"
}
```

***Example 8 (Removal only)***

****Input JSON****
```
{
  "names": null,
  "coordinates": null,
  "firstName": "John",
  "lastName": "Smith",
  "fullName": "John Smith",
  "age": 31,
  "city": "New York",
  "contacts": {
    "homePhone": [
      "111",
      "000",
      "234a"
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
      "unwantedFields": [
        "fullName"
      ]
    },
    {
      "unwantedFields": [
        "contacts.homePhone",
        "contacts.officePhone"
      ]
    },
    {
      "unwantedFields": [
        "names",
        "coordinates"
      ]
    },
    {
      "unwantedFields": [
        "contacts"
      ]
    }
  ]
}
}
```
The "_fullName", "names", "coordinates", "contacts" fields are simply removed.

****Output JSON****
```
{
  "firstName": "John",
  "lastName": "Smith",
  "age": 31,
  "city": "New York"
}
```


