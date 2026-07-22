# Project: Minesweeper

## MVP

Recreate a simplified version of the game Minesweeper to be played in the java console
The game should be able to randomly generate 10 mines in a 10x10 grid
The user will be able to enter a command that represents a coordinate to check a location for a mine
The application will display a number from 0-8 depending on how many mines surround that location
If the user selects a mine, the game will respond "boom!" and the game will be lost
If every non-mine square has been revealed, the game is won
Render the grid to the console after every user command

## Bonuses (optional)

1. Allow for the user to configure number of mines and grid size via a configuration.
2. (Difficult) Discovering an empty square should reveal all squares around it, and cascade into other nearby empty squares

[
[cell00, cell01, cell 02],
[cell 1,0, cell11],
[cell 2,0 ],
[],
[],
[]
]

```bash
[0,0][0,1 ] [0,2]
[ 1,0][1,1] [1,2]
[ 2,0][2,1] [2,2]
```

```bash
[ ][1][ ]
[ ][ ][ ]
[ ][ ][ ]
```

```bash
+---+---+
| 2 |   |
+---+---+
```
