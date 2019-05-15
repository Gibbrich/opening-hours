There are several basic/special test cases:
1. Basic (restaurant opens and closes at the same day)
2. Open-close several times per day
3. Start with close operation
4. End with open operation
5. Closed whole day
6. Opened whole day
7. Opened several days
8. Empty
9. Opened at the evening, closed at the morning in a week

All these cases are listed in /src/test/resources/integration/basic

Cases in /src/test/resources/integration/random built from these basic cases
in random order to check they work well together

Cases in /src/test/resources/invalid_input contains invalid input,
which check robustness of the solution in general