Super Secret Projet
===================

Juste a small **NOT SECRET** 2D game for those who want to see how a small Java 2D game can be created  
In the game you control a soldier ; zombies appear randomly and you can kill them


Classes Structure
---------------

The class **Main** is just the main

The class **Window** represents the window and contains the framework  
The class **Framework** extends the **Canvas** class, it starts the game and represents the logic of one Game Loop  
The class **Canvas** is an helper to catch keybord and mouse events for the game

The class **Game** respresents the logic and the structure of our game, for each frames the <code>updateGame()</code> and <code>draw()</code> methods are called

The class **Player** represents the character that you can control  
The class **Zombie** represents one of the zombies that the computer control  
The class **Fire** represents one of the shots that the player (you) fired

The class **Ressources** is an helper to get ressources (as images)
