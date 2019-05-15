# opening-hours

## How to run program
    ./gradlew run --args=‘arg1 arg2’
Where:
* arg1 - path to file with input (mandatory)
* arg2 - path to file, where program will write results (optional).
If program will run with 1 argument, output will be printed in the console.

## How to test
    ./gradlew test
Launches all unit tests

## Corner cases and assumptions:

During implementing solution, I faced several corner cases, which were not highlighted in assignment. I asked about these cases but didn’t receive any additional conditions/limitations on implementations, so I made several assumptions, which do not conflict with initial requirements. Below listed these cases and my assumptions on them:

1. Restaurant can work more than 24h
Let’s imagine following situation: restaurant opens on Friday at 18.00 and closes on Monday at 01.00. There is no more working hours on Monday. In json it will look like this (partially):

<b>Example 1.</b>

Input:

```json
{
"monday": [
  {
    "type": "close",
    "value": 3600
  }
],
...
"friday": [
  {
    "type": "open",
    "value": 64800
  }
],
"saturday": ?,
"sunday": ?,
}
```

There are several subsequent questions about this example:
1. How should be displayed information about working time on Friday, Saturday, Sunday and Monday?
2. In original requirements, first closing operation in the day operations list belongs to previous day. In this case, closing operation on Monday must belong to Friday. But how display correctly, Friday will close on Monday? And how to display Monday then? We can’t display just “Closed”, as it will be incorrect
3. Which values will be in field “saturday” and “sunday” in json in this case?

Assumptions, that I made to handle all these corner cases are:
1. If restaurant opens on one day and closes other day and there is at days between open and start, json fields for these days will contain empty lists. 
2. If there is at least 1 day gap between start and end, the display string will be like this <day_of_week>: <start_time> - <close_time> at <close_day_of_week>. This is done to provide clear information, when restaurant will be closed.
3. If the day contains only close operation, it will be displayed as <day_of_week>: 12 AM - <close_time>. If day contains not only close operation, first close operation will not be displayed, instead standard rules will be applied (see input and output in “Example 2”)

Generally, output for Example 1 will be displayed like this (partly):

Monday: 12 AM - 1 AM

…

Friday: 6 PM - 1 AM at Monday

Saturday: Open whole day

Sunday: Open whole day

<b>Example 2.</b>

Input (partly):

```json
{
"monday": [
  {
    "type": "open",
    "value": 36000
  },
  {
    "type": "close",
    "value": 64800
  },
  {
    "type": "open",
    "value": 75600
  }
],
"tuesday": []
"wednesday": [
  {
    "type": "close",
    "value": 3600
  },
  {
    "type": "open",
    "value": 36000
  },
  {
    "type": "close",
    "value": 64800
  }
]
…
}
```

Output (partly):

Monday: 10 AM - 6 PM, 9 PM - 1 AM at Wednesday

Tuesday: Opened whole day

Wednesday: 10 AM - 6 PM

...

2. There is no limitation on how long restaurant can work, so theoretically it can open on one day and close on this day, but week later. In this case day info will be displayed like this: <day_of_week>: <open_time> - <close_time> at next <day_of_week>. Example in “Example 3”

<b>Example 3.</b>

Input:

```json
{
  "monday": [
    {
      "type": "close",
      "value": 36000
    },
    {
      "type": "open",
      "value": 64800
    }
  ],
  "tuesday": [],
  "wednesday": [],
  "thursday": [],
  "friday": [],
  "saturday": [],
  "sunday": []
}
```

Output:

Monday: 6 PM - 10 AM at next Monday

Tuesday: Opened whole day

Wednesday: Opened whole day

Thursday: Opened whole day

Friday: Opened whole day

Saturday: Opened whole day

Sunday: Opened whole day


In general, I followed all restrictions listed in the assignment and tried on my own to extend solution to handle corner cases. This extension does not conflict with conditions in assignment. If the solution to corner cases seems not to be elegant or overcomplicated, I can change it or, rewrite once I will receive relevant instructions.

## How API can be improved:
Honestly speaking, JSON scheme in assignment is very inconvenient for parsing. The main problems are:
1. For understanding, when restaurant actually closes, we need to iterate through next days and find first closing operation
2. To iterate through all days in this data structure, we need to write custom iterator or use another non-trivial technique
3. It is not very easy to extend scheme, if there will be some changes (e.g. type of “monday” field is list, which can not be extended)
4. It can send unused data - if some day will have empty list of data

Listed below scheme fixes these problems. 
1. It provides convenient iteration, as all days are in the list 
2. All operations have day of the week, when they start/end
3. All values wrapped inside extendible objects, which is convenient for backwards compatibility
4. If some days have no operations, they just can be excluded from the list.

```json
{
  "data": [
    {
      "day": "monday",
      "workingHours": [
        {
          "operation": "open",
          "value": 3600,
          "day": "monday"
        },
        {
          "operation": "close",
          "value": 3600,
          "day": "tuesday"
        }
      ]
    },
    {
      "day": "tuesday",
      "workingHours": [
        {
          "operation": "open",
          "value": 36000,
          "day": "tuesday"
        },
        {
          "operation": "close",
          "value": 72000,
          "day": "tuesday"
        }
      ]
    }
  ]
}
```
