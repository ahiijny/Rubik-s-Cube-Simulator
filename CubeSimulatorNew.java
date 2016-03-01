// TODO: Convert to JFrame and out of hsa.Console
// TODO: Split into multiple files

// The "CubeSimulatorNew" class.
import java.awt.*;
import hsa.Console;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.Graphics;               //Used to draw to the backbuffer
import java.awt.Color;                  //Used to set the color of the Graphics2D class
import java.awt.image.BufferedImage;    //Used as the backbuffer

// Name:        CubeSimulatorNew
// Date(due):   CE 2013/01/21(Monday), 09:00 EST (UTC-5)
// Author:      ahiijny
// Description:
//      This program simulates various Rubik's cubes, including
//      a 0x0x0 cube, a 1x1x1 cube, a 2x2x2 cube, and a 3x3x3 cube.

public class CubeSimulatorNew
{
    //---------------------------//
    // DECLARATION OF CONSTANTS  //
    //---------------------------//

    static final String[] faceNames =
	{"red",     // 0
	"yellow",   // 1
	"blue",     // 2
	"white",    // 3
	"green",    // 4
	"orange"},  // 5

	orientationNames =
	{"right",  // 0
	"left",    // 1
	"down",    // 2
	"up",      // 3
	"back",    // 4
	"front"},  // 5

	gameCtrlNames =
	{" R", " R'",
	" L", " L'",
	" D", " D'",
	" U", " U'",
	" B", " B'",
	" F", " F'",
	"scr",
	" x", " x'",
	" y", " y'",
	" z", " z'",
	"nud", "hue", "ctrls",
	"\u2191", "\u2193",
	"\u2190", "\u2192",
	"can", "sel", "start"
	};

    static final int[] [] oppositeSides =
	{{0, 1, 2, 3, 4, 5}, {5, 3, 4, 1, 2, 0}},
	rotateCenterSheet =
	{{ - 9, -9, 5, 4, 2, 3},  // right clockwise turn
	    { - 9, -9, 4, 5, 3, 2},  // right counterclockwise turn
	    {5, 4, -9, -9, 0, 1},  // up clockwise turn
	    {4, 5, -9, -9, 1, 0},  // up counterclockwise turn
	    {2, 3, 1, 0, -9, -9},  // front clockwise turn
	    {3, 2, 0, 1, -9, -9}}; // front counterclockwise turn

    static final int[] useCenter = {1, 2, 3, 4},
	useCorner1 = {0, 1, 2, 3},
	useCorner2 = {4, 5, 6, 7},
	useCorner3 = {8, 9, 10, 11},
	txOffsets = {0, 225, 230, 60},
	tyOffsets = new int [4],
	menuLengths = {0, 4, 4, 4};
    // cornerIdList = {23, 12, 14, 34, 235, 125, 145, 345};
    // edgeIdList = {3, 2, 1, 4, 23, 12, 14, 13, 35, 25, 15, 45};


    static final int h = 320, k = 250,
	// ^ h and k are the centre of the rotational
	// axes on the HSA console screen
	consoleWidth = 640,
	consoleHeight = 600,
	ln1 = 30,
	ln2 = 40;

    static final Color[] colorBank =
	{Color.red,
	Color.yellow,
	Color.blue,
	Color.white,
	Color.green,
	new Color (255, 160, 0) },  // orange

	presetBackgrounds = {Color.white,
	new Color (220, 220, 220),  // table grey
	new Color (222, 227, 237),  // xkcd light blue
	new Color (210, 221, 236),  // qntm light blue        
	new Color (210, 226, 205),  // Futaba Tsukasa light green
	new Color (186, 220, 200),  // Weboshi light green
	new Color (255, 255, 180),  // Yellow light yellow
	new Color (247, 236, 202),  // Annaka light yellow
	new Color (253, 220, 189),  // Crunchyroll light orange
	new Color (244, 208, 206),  // YouTube light red
	new Color (231, 193, 190),  // Tachibana Misato light red
	new Color (226, 209, 248)}; // Hiiragi Tsukasa light purple        

    static final Color darkRed = new Color (80, 0, 0),
	darkYellow = new Color (80, 80, 0),
	darkBlue = new Color (0, 0, 80),
	cursorColor = new Color (153, 204, 0),
	greyCursorColor = new Color (65, 88, 0);

    static final Font fontTitle = new Font ("Arial Black", Font.BOLD, 60),
	fontMenuBold = new Font ("Courier", Font.BOLD, 16),
	fontMenuPlain = new Font ("Courier", Font.PLAIN, 16),
	fontQwerty = new Font ("Times New Roman", Font.PLAIN, 9),
	fontDefault = new Font ("Arial", Font.PLAIN, 14);

    static final char nul = '\u0000',

	// GAME CONTROL KEYS

	ctrlR = 'r',  // Right
	ctrlR1 = 'v',  // Right Inverted
	ctrlL = 'z',  // Left
	ctrlL1 = 'q',  // Left Inverted
	ctrlD = 'x',  // Down
	ctrlD1 = 'c',  // Down Inverted
	ctrlU = 'e',  // Up
	ctrlU1 = 'w',  // Up Inverted
	ctrlB = 's',  // Back
	ctrlB1 = 'd',  // Back Inverted
	ctrlF = 'f',  // Front
	ctrlF1 = 'a',  // Front Inverted
	ctrlScram = '\b',  // Scramble cube
	ctrlX = '8',  // x
	ctrlX1 = '2',  // x Inverted
	ctrlY = '3',  // y
	ctrlY1 = '1',  // y Inverted
	ctrlZ = '6',  // z
	ctrlZ1 = '4',  // z inverted
	ctrlNudge = '+',  // Toggle nudge mode
	ctrlGround = '*',  // Switch background
	ctrlCtrl = 'i',  // Toggle show controls

	// MENU CONTROL KEYS

	ctrlUp = 'w',
	ctrlDown = 's',
	ctrlLeft = 'a',
	ctrlRight = 'd',
	ctrlGbaB = '.',
	ctrlGbaA = '\n',
	ctrlStart = ' ';

    static final char[] [] controlOpposites =
	{
	    {
	    ctrlR, ctrlR1, ctrlL, ctrlL1, ctrlD, ctrlD1,  // gameMode >= 2
	    ctrlU, ctrlU1, ctrlB, ctrlB1, ctrlF, ctrlF1,
	    ctrlScram,
	    ctrlX, ctrlX1, ctrlY, ctrlY1, ctrlZ, ctrlZ1,  // gameMode >= 1
	    ctrlNudge,
	    ctrlGround, ctrlCtrl,  // gameMode >= 0
	    ctrlUp, ctrlDown, ctrlLeft, ctrlRight,  // menus
	    ctrlGbaB, ctrlGbaA, ctrlStart
	}
	,
	    {
	    ctrlR1, ctrlR, ctrlL1, ctrlL, ctrlD1, ctrlD,
	    ctrlU1, ctrlU, ctrlB1, ctrlB, ctrlF1, ctrlF
	}
	},

	qwerty =
	{

	    {
	    '`', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '-', '=', '\b'
	}
	,
	    {
	    nul, 'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P', '[', ']', '\\'
	}
	,
	    {
	    nul, 'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', ';', '\'', '\n', nul
	}
	,
	    {
	    nul, 'Z', 'X', 'C', 'V', 'B', 'N', 'M', ',', '.', '/', nul, nul, nul,
	}
	,
	    {
	    nul, nul, nul, ' ', nul, nul, nul, nul, nul, nul, nul, nul, nul, nul
	}
	},

	numpad =
	{
	    {
	    nul, '/', '*', '-'
	}
	,
	    {
	    '7', '8', '9', '+'
	}
	,
	    {
	    '4', '5', '6', nul
	}
	,
	    {
	    '1', '2', '3', '\n'
	}
	,
	    {
	    '0', nul, '.', nul
	}
	}
	;


    //---------------------------//
    // DECLARATION OF VARIABLES  //
    //---------------------------//

    static double[] [] centerRed = new double [5] [7],
	centerYellow = new double [5] [7],
	centerBlue = new double [5] [7],
	centerWhite = new double [5] [7],
	centerGreen = new double [5] [7],
	centerOrange = new double [5] [7],
	/* ^ center [vertex number][coordinate identifier]
	    [...][0] = color identifier
	    [...][1] = x0 coordinate
	    [...][2] = y0 coordinate
	    [...][3] = z0 coordinate
	    [...][4] = x1 coordinate
	    [...][5] = y1 coordinate
	    [...][6] = z1 coordinate */

	cornerRedBlueWhite = new double [12] [8],
	cornerRedYellowBlue = new double [12] [8],
	cornerRedYellowGreen = new double [12] [8],
	cornerRedWhiteGreen = new double [12] [8],
	cornerBlueWhiteOrange = new double [12] [8],
	cornerYellowBlueOrange = new double [12] [8],
	cornerYellowGreenOrange = new double [12] [8],
	cornerWhiteGreenOrange = new double [12] [8],
	/* ^ corner [vertex number][coordinate identifier]
	    [...][0] color identifier
	    [...][1] = x0 coordinate
	    [...][2] = y0 coordinate
	    [...][3] = z0 coordinate
	    [...][4] = x1 coordinate
	    [...][5] = y1 coordinate
	    [...][6] = z1 coordinate
	    [...][7] = connected to which center */

	edgeRedWhite = new double [8] [8],
	edgeRedBlue = new double [8] [8],
	edgeRedYellow = new double [8] [8],
	edgeRedGreen = new double [8] [8],
	edgeBlueWhite = new double [8] [8],
	edgeYellowBlue = new double [8] [8],
	edgeYellowGreen = new double [8] [8],
	edgeWhiteGreen = new double [8] [8],
	edgeWhiteOrange = new double [8] [8],
	edgeBlueOrange = new double [8] [8],
	edgeYellowOrange = new double [8] [8],
	edgeGreenOrange = new double [8] [8],

	control = new double [4] [4],
	pureXYZ = new double [4] [4],
	deltaPrimes = new double [4] [4];

    static double[] tempCoefficients = new double [4];

    static double hypotenuseX1,
	hypotenuseY1, hypotenuseZ1,
	axy, bxy, cxy, dz,
	axz, bxz, cxz, dy,
	ayz, byz, cyz, dx,
	et = Math.PI / 48,
	slowSpin = Math.PI / 48,
	fastSpin = Math.PI / 80,  // Denominator must be an even number
	currentSpin = et;

    static int[] [] centerOwnership = new int [6] [4],
	tempCenterOwnership = new int [centerOwnership.length] [centerOwnership [0].length],
	cornerIndexes = new int [6] [2],
	edgeIndexes = new int [6] [1];

    static int[] r = {100, 33, 99},
	cursor = {0, 1, 2, 0},
	cornersThatNeedRotating = new int [4],
	edgesThatNeedRotating = new int [4],
	order = new int [26],
	suborder = new int [3],
	xPoints = new int [10],
	yPoints = new int [10],
	zPoints = new int [10],
	orientation = new int [6];
    //    ^ right, left, down, up, back, front

    static int whichBack = 0,
	counter = 0,
	shift = 0,
	tx, ty,
	choice = -1,
	index,
	fastSpinStop = (int) (1 / (2 * fastSpin / Math.PI) - 1);

    static Color faceColor,
	background = presetBackgrounds [whichBack];

    static char lastInput, thisInput, input;

    static byte gameMode = 0;

    static boolean right,
	isRunning = true,
	isGaming = false,
	skipToSelection = false,
	isInstructing = true,
	gameInstructing = true,
	randomizing = false,
	nudging = true,
	turning = false,
	needConverting;


    //-----------------------------------//
    // Consoles and Buffered Image Stuff //
    //-----------------------------------//

    static BufferedImage bi = new BufferedImage
	(consoleWidth, consoleHeight, BufferedImage.TYPE_INT_RGB);

    static Graphics g = (Graphics) bi.createGraphics ();

    static Console c; // The output console


    //---------------------//
    //       METHODS       //
    //---------------------//

    public static void main (String[] args)  // main method
    {
	c = new Console (30, 80);

	// DECLARATION OF CONSTANTS AND VARIABLES

	final char[] acceptable = new char[]
	{
	    ctrlUp, ctrlUp, ctrlDown, ctrlDown, ctrlLeft, ctrlRight, ctrlLeft, ctrlRight, ctrlGbaB, ctrlGbaA, ctrlStart
	}
	;

	SimpleDateFormat df = new SimpleDateFormat ("G yyyy/MM/dd(EEEE), HH:mm:ss.SSS z ("),
	    zf = new java.text.SimpleDateFormat ("Z)");
	String date, zone;

	char[] lastInputs = new char [11];
	long startTime;
	int index, randIndex = 0, lastRandIndex = 0, loopCount = 0, i, j;
	boolean isCorrect;

	while (isRunning) // program loop
	{
	    if (!skipToSelection) // skip = goes straight to cube selection
	    {
		// INPUT

		// Title screen

		tx = 160;
		ty = 120;
		drawTitleScreen ();
		lastInputs = new char [11];
		isCorrect = false;
		index = -1;

		do
		{
		    // User input

		    if (c.isCharAvail ())
		    {
			input = Character.toLowerCase (c.getChar ());

			if (index < lastInputs.length - 1)
			    index++;

			else
			    for (i = 1 ; i < lastInputs.length ; i++)
				lastInputs [i - 1] = lastInputs [i];

			lastInputs [index] = input;
		    }

		    // "Press Space" flash

		    if (loopCount % 40 == 20)
			c.clearRect (tx - 2, ty - 14, 120, 15);

		    else if (loopCount % 40 == 0)
			c.drawString ("PRESS SPACE", tx, ty);

		    loopCount++;

		    try
		    {
			Thread.sleep (25);
		    }
		    catch (InterruptedException ie)
		    {
		    }
		}
		while (input != ctrlStart);

		c.clearRect (tx - 2, ty - 14, 120, 15);


		// Input validation
		
		isCorrect = true;

		for (i = 0 ; i < acceptable.length; i++)
		{
		    if (acceptable [i] != lastInputs [i])
		    {
			isCorrect = false;
			break;
		    }
		}

		if (isCorrect)
		{
		    isCorrect = false;
		    easterEgg ();
		    tx = 160;
		    ty = 120;
		    drawTitleScreen ();
		}

		// Title screen choice

		ty -= 40;
		c.drawString ("NEW GAME", tx, ty);
		ty += ln1;
		c.drawString ("INSTRUCTIONS", tx, ty);
		ty += ln1;
		c.drawString ("ABOUT", tx, ty);
		ty += ln1;
		c.drawString ("QUIT", tx, ty);

		cursor [0] = 1;
		tyOffsets [1] = cursor [1] * ln1 + 333;
		drawCursor (txOffsets [1], tyOffsets [1]);
		do
		    navigate (ln1, 333, Color.white);
		while (input != ctrlGbaA && input != ctrlStart && input != ctrlGbaB);

		if (input == ctrlGbaB)
		    choice = -1;
	    }
	    else // skipToSelection
	    {
		choice = 0;
		skipToSelection = false;
	    }

	    if (choice == 0) // NEW GAME
	    {
		// Game mode choice

		tx = 210;
		ty = 120;
		c.setFont (fontMenuPlain);
		c.clear ();

		c.drawString ("Please choose a cube:", tx, ty);
		ty += ln2 + ln2;
		c.drawString ("     0 x 0 x 0", tx, ty);
		ty += ln2;
		c.drawString ("     1 x 1 x 1", tx, ty);
		ty += ln2;
		c.drawString ("     2 x 2 x 2", tx, ty);
		ty += ln2;
		c.drawString ("     3 x 3 x 3", tx, ty);

		cursor [0] = 2;
		tyOffsets [2] = cursor [2] * ln2 + 190;
		drawCursor (txOffsets [2], tyOffsets [2]);

		do
		    navigate (ln2, 190, Color.white);
		while (input != ctrlGbaA && input != ctrlStart && input != ctrlGbaB);

		if (input == ctrlGbaB)
		{
		    choice = -1;
		    input = ' ';
		}

		// Game Loops

		gameInstructing = true;
		nudging = true;
		lastInput = '-';
		thisInput = '-';

		if (choice == 0) // 0x0x0 Cube
		{
		    isGaming = true;
		    gameMode = 0;
		    metaZeroDraw ();

		    // Input loop

		    while (isGaming)
		    {
			input = Character.toLowerCase (c.getChar ());

			// Input Determination

			if (input == ctrlGround)
			{
			    whichBack++;
			    if (whichBack >= presetBackgrounds.length)
				whichBack = 0;
			    background = presetBackgrounds [whichBack];
			}
			else if (input == ctrlCtrl)
			{
			    if (isInstructing)
			    {
				if (gameInstructing)
				    gameInstructing = false;
				else
				    isInstructing = false;
			    }
			    else
			    {
				isInstructing = true;
				gameInstructing = true;
			    }
			}
			else if (input == ctrlStart)
			    pause ();


			metaZeroDraw ();
		    }
		} // done 0x0x0 Cube

		else if (choice == 1) // 1x1x1 Cube
		{
		    isGaming = true;
		    gameMode = 1;
		    initializeOneCube ();
		    faceDeterminer ();
		    metaOneDraw ();


		    // Input loop

		    while (isGaming)
		    {
			faceDeterminer ();

			if (!turning)
			    input = Character.toLowerCase (c.getChar ());
			else
			    counter++;

			if (c.isCharAvail ())
			    c.getChar ();

			// Input Determination

			if (input == ctrlGround)
			{
			    whichBack++;
			    if (whichBack >= presetBackgrounds.length)
				whichBack = 0;
			    background = presetBackgrounds [whichBack];
			}
			else if (input == ctrlNudge)
			{
			    nudging = !nudging;

			    if (nudging)
				currentSpin = et = slowSpin;
			    else
				currentSpin = et = fastSpin;
			}
			else if (input == ctrlCtrl)
			{
			    if (isInstructing)
			    {
				if (gameInstructing)
				    gameInstructing = false;
				else
				    isInstructing = false;
			    }
			    else
			    {
				isInstructing = true;
				gameInstructing = true;
			    }
			}
			else if (input == ctrlStart)
			    pause ();

			else
			{
			    identifyInput (input);

			    if (!nudging)
				turning = true;
			}

			metaOneDraw ();

			if (counter >= fastSpinStop)
			{
			    turning = false;
			    counter = 0;
			}
		    }
		} // done 1x1x1 Cube

		else if (choice == 2) // 2x2x2 Cube
		{
		    isGaming = true;
		    gameMode = 2;
		    initializeTwoCube ();
		    faceDeterminer ();
		    metaTwoDraw ();

		    // Input loop

		    while (isGaming)
		    {
			faceDeterminer ();

			if (!turning && !randomizing)
			    input = Character.toLowerCase (c.getChar ());

			else if (!turning && randomizing)
			{
			    do
				randIndex = (int) (12 * Math.random ());
			    while (controlOpposites [0] [randIndex] == controlOpposites [1] [lastRandIndex]);
			    input = controlOpposites [0] [randIndex];
			    lastRandIndex = randIndex;
			}
			else
			    counter++;

			// Input Determination

			if (c.isCharAvail ())
			{
			    randomizing = false;
			    c.getChar ();
			}

			if (input == ctrlScram)
			    randomizing = true;

			else if (input == ctrlGround)
			{
			    whichBack++;
			    if (whichBack >= presetBackgrounds.length)
				whichBack = 0;
			    background = presetBackgrounds [whichBack];
			}
			else if (input == ctrlNudge)
			{
			    nudging = !nudging;

			    if (nudging)
				currentSpin = et = slowSpin;
			    else
				currentSpin = et = fastSpin;
			}
			else if (input == ctrlCtrl)
			{
			    if (isInstructing)
			    {
				if (gameInstructing)
				    gameInstructing = false;
				else
				    isInstructing = false;
			    }
			    else
			    {
				isInstructing = true;
				gameInstructing = true;
			    }
			}
			else if (input == ctrlStart)
			    pause ();

			else
			{
			    identifyInput (input);

			    if (!nudging)
				turning = true;
			}

			metaTwoDraw ();

			if (counter >= fastSpinStop)
			{
			    turning = false;
			    counter = 0;
			}
		    }
		} // done 2x2x2 Cube

		else if (choice == 3) // 3x3x3 Cube
		{
		    isGaming = true;
		    gameMode = 3;
		    initializeThreeCube ();
		    faceDeterminer ();
		    metaThreeDraw ();

		    // Input loop

		    while (isGaming)
		    {
			faceDeterminer ();

			if (!turning && !randomizing)
			    input = Character.toLowerCase (c.getChar ());

			else if (!turning && randomizing)
			{
			    do
				randIndex = (int) (12 * Math.random ());
			    while (controlOpposites [0] [randIndex] == controlOpposites [1] [lastRandIndex]);
			    input = controlOpposites [0] [randIndex];
			    lastRandIndex = randIndex;
			}
			else
			    counter++;

			// Input Determination

			if (c.isCharAvail ())
			{
			    randomizing = false;
			    c.getChar ();
			}
			if (input == ctrlScram)
			    randomizing = true;

			else if (input == ctrlGround)
			{
			    whichBack++;
			    if (whichBack >= presetBackgrounds.length)
				whichBack = 0;
			    background = presetBackgrounds [whichBack];
			}
			else if (input == ctrlNudge)
			{
			    nudging = !nudging;

			    if (nudging)
				currentSpin = et = slowSpin;
			    else
				currentSpin = et = fastSpin;
			}
			else if (input == ctrlCtrl)
			{
			    if (isInstructing)
			    {
				if (gameInstructing)
				    gameInstructing = false;
				else
				    isInstructing = false;
			    }
			    else
			    {
				isInstructing = true;
				gameInstructing = true;
			    }
			}
			else if (input == ctrlStart)                        
			    pause ();
			
			else
			{
			    identifyInput (input);

			    if (!nudging)
				turning = true;
			}

			metaThreeDraw ();

			if (counter >= fastSpinStop)
			{
			    turning = false;
			    counter = 0;
			}
		    }
		} // done 3x3x3 Cube

	    } // done "new game" loop

	    else if (choice == 1) // "INSTRUCTIONS"
	    {
		isInstructing = true;
		gameInstructing = false;
		do
		{
		    c.clear ();
		    g.setColor (Color.white);
		    g.fillRect (0, 0, consoleWidth, consoleHeight);
		    drawInstructions (3);
		    c.drawImage (bi, 0, -375, null);

		    c.setCursor (13, 1);
		    if (gameInstructing)
		    {
			c.println ("'ctrls' toggles the keyboard control display.");
			c.println ("'scr' scrambles the cube.");
			c.println ("'hue' toggles the background color in-game.");
			c.println ("'nud' toggles nudge mode.");
		    }
		    else
		    {
			c.println ("'ctrls' toggles the keyboard control display.");
			c.println ("'sel' selects an option. 'start' also does.");
			c.println ("'can' cancels and goes back.");

		    }
		    c.println ("Press \'" + ctrlCtrl + "\' now!");
		    c.setCursor (19, 1);
		    c.println ("This diagram will be visible in-game.");
		    c.println ("\n\n\n\nPlease excuse the crudity of this keyboard diagram.");
		    c.println ("I didn't have time to build it to scale or paint it.");

		    c.setCursor (27, 65);
		    c.print ("Go back");
		    drawCursor (490, 524);
		    input = c.getChar ();
		    if (input == ctrlCtrl)
			gameInstructing = !gameInstructing;
		}
		while (input != ctrlGbaB && input != ctrlStart && input != ctrlGbaA);

		input = ctrlStart;
	    }

	    else if (choice == 2) // "ABOUT"
	    {
		c.clear ();
		c.println ("CubeSimulatorNew v3.\u03B8, by Jiayin Huang\n\n");
		c.println ("Created for an ICS Summative due on:");
		c.println ("AD 2013/01/21(Monday), 09:00:00.000 EST (UTC-0500)");
		c.println ("\nThe current time is:");
		c.setCursor (27, 65);
		c.print ("Go back");
		drawCursor (490, 524);
		c.setCursor (8, 1);

		do
		{
		    while (!c.isCharAvail ()) // display date loop
		    {
			startTime = System.currentTimeMillis ();
			c.setCursor (c.getRow (), 1);
			date = df.format (new Date (startTime));
			zone = zf.format (new Date (startTime));

			c.print (date);
			c.print ("UTC" + zone);
			while (System.currentTimeMillis () < startTime + 33)
			{
			    try
			    {
				Thread.sleep (15);
			    }
			    catch (InterruptedException ie)
			    {
			    }
			}
		    }
		    input = c.getChar ();
		}
		while (input != ctrlStart && input != ctrlGbaA && input != ctrlGbaB);

		input = ctrlStart;
	    }

	    else if (choice == 3) // "QUIT"
	    {
		isRunning = false;
	    }
	} // done program loop

	c.close ();

    } // main method


    public static void drawTitleScreen ()
    {
	// Title screen

	c.clear ();
	c.setFont (fontTitle);

	c.setColor (darkRed);
	c.drawString ("Rubik's", tx, ty);
	ty -= 2;
	tx -= 2;
	c.setColor (Color.red);
	c.drawString ("Rubik's", tx, ty);
	ty += 72;
	tx += 2;

	c.setColor (darkYellow);
	c.drawString ("Cube", tx, ty);
	ty -= 2;
	tx -= 2;
	c.setColor (Color.yellow);
	c.drawString ("Cube", tx, ty);
	ty += 72;
	tx += 2;

	c.setColor (darkBlue);
	c.drawString ("Simulator", tx, ty);
	ty -= 3;
	tx -= 3;
	c.setColor (Color.blue);
	c.drawString ("Simulator", tx, ty);
	ty += 130;
	tx += 110;

	c.setFont (fontMenuBold);
	c.setColor (Color.black);

    } // drawTitleScreen method


    public static void drawInstructions (int gameMode)
    {
	final int x1 = 10, y = 440, x2 = 500, w = 33, h = 30;
	int i, j, k, cX, cY, startIndex, stopIndex;

	g.setColor (Color.white);
	g.fillRect (x1, y, w * 14, h * 5);
	g.fillRect (x2, y, w * 4, h * 5);
	g.setColor (Color.black);

	// Title

	g.setFont (fontDefault);
	if (gameInstructing)
	    g.drawString ("In-game controls:", x1, y - 16);
	else
	    g.drawString ("Menu navigation controls:", x1, y - 16);


	// Keyboard (left side)

	for (i = 0 ; i <= 14 ; i++) // vertical lines
	{
	    cX = x1 + (i * w);
	    cY = y + 5 * h;
	    g.drawLine (cX, y, cX, cY);
	}

	g.setColor (Color.white); // long buttons cover up
	g.fillRect (x1 + (4 * w), y + (4 * h), (6 * w) - 1, h);
	g.fillRect (x1 + (13 * w), y + (2 * h), (1), h);
	g.fillRect (x1 + (12 * w), y + (3 * h), (w + 1), h);
	g.setColor (Color.black);

	for (i = 0 ; i <= 5 ; i++) // horizontal lines
	{
	    cX = x1 + (14 * w);
	    cY = y + (i * h);
	    g.drawLine (x1, cY, cX, cY);
	}


	// Numpad (right side)

	for (i = 0 ; i <= 4 ; i++) // vertical lines
	{
	    cX = x2 + (i * w);
	    cY = y + 5 * h;
	    g.drawLine (cX, y, cX, cY);
	}

	for (i = 0 ; i <= 5 ; i++) // horizontal lines
	{
	    cX = x2 + (4 * w);
	    cY = y + (i * h);
	    g.drawLine (x2, cY, cX, cY);
	}

	g.setColor (Color.white); // long buttons cover up
	g.fillRect (x2 + (3 * w) + 1, y + (2 * h), w - 1, 1);
	g.fillRect (x2 + (3 * w) + 1, y + (4 * h), w - 1, 1);
	g.fillRect (x2 + (w), y + (4 * h) + 1, 1, h - 1);
	g.setColor (Color.black);


	// The Qwerty Keyboard

	if (gameInstructing)
	{
	    if (gameMode == 0)
		startIndex = 20;
	    else if (gameMode == 1)
		startIndex = 13;
	    else
		startIndex = 0;
	    stopIndex = 22;
	}
	else
	{
	    startIndex = 21;
	    stopIndex = 29;
	}

	g.setColor (new Color (180, 180, 180));
	g.setFont (fontQwerty);

	for (i = 0 ; i < qwerty.length ; i++)
	{
	    tx = x1 + 5;
	    ty = y + 10 + (i * h);
	    for (j = 0 ; j < qwerty [i].length ; j++) // keyboard letters
	    {
		if ((int) qwerty [i] [j] > 32)
		    g.drawString ("" + qwerty [i] [j], tx, ty);
		else if (qwerty [i] [j] == '\b')
		    g.drawString ("\u2190", tx, ty);
		else if (qwerty [i] [j] == '\n')
		    g.drawString ("Enter", tx, ty);

		for (k = startIndex ; k < stopIndex ; k++) // keyboard controls
		{
		    if (controlOpposites [0] [k] == Character.toLowerCase (qwerty [i] [j])
			    && !Character.isDigit (qwerty [i] [j])
			    && qwerty [i] [j] != '/'
			    && qwerty [i] [j] != '-'
			    && qwerty [i] [j] != '.'
			    && qwerty [i] [j] != '\n')
		    {
			g.setFont (fontDefault);
			g.setColor (Color.black);
			g.drawString (gameCtrlNames [k], tx, ty + 12);
			g.setColor (new Color (180, 180, 180));
			g.setFont (fontQwerty);
		    }
		}

		if (controlOpposites [0] [28] == qwerty [i] [j]
			&& gameInstructing) // special case "start = pause menu"
		{
		    g.setFont (fontDefault);
		    g.setColor (Color.black);
		    g.drawString ("pause menu", tx, ty + 12);
		    g.setColor (new Color (180, 180, 180));
		    g.setFont (fontQwerty);
		}

		tx += w;
	    }
	}


	// The Numpad

	for (i = 0 ; i < numpad.length ; i++) // keyboard letters
	{
	    tx = x2 + 5;
	    ty = y + 10 + (i * h);
	    for (j = 0 ; j < numpad [i].length ; j++)
	    {
		if ((int) numpad [i] [j] > 32)
		    g.drawString ("" + numpad [i] [j], tx, ty);
		else if (numpad [i] [j] == '\n')
		    g.drawString ("Enter", tx, ty);

		for (k = startIndex ; k < stopIndex ; k++) // keyboard controls
		{
		    if (controlOpposites [0] [k] == Character.toLowerCase (numpad [i] [j]))
		    {
			g.setFont (fontDefault);
			g.setColor (Color.black);
			g.drawString (gameCtrlNames [k], tx, ty + 12);
			g.setColor (new Color (180, 180, 180));
			g.setFont (fontQwerty);
		    }
		}
		tx += w;
	    }
	}
    } // drawInstructions method


    public static void navigate (int ln, int start, Color erase)
	// ^ invoked when the user is navigating a menu.
    {
	input = Character.toLowerCase (c.getChar ());
	if (input == ctrlUp) // move cursor up
	{
	    cursor [cursor [0]]--;
	    if (cursor [cursor [0]] < 0)
		cursor [cursor [0]] = menuLengths [cursor [0]] - 1;
	}

	else if (input == ctrlDown) // move cursor down
	{
	    cursor [cursor [0]]++;
	    if (cursor [cursor [0]] >= menuLengths [cursor [0]])
		cursor [cursor [0]] = 0;
	}

	eraseCursor (txOffsets [cursor [0]], tyOffsets [cursor [0]], erase);
	tyOffsets [cursor [0]] = cursor [cursor [0]] * ln + start;
	drawCursor (txOffsets [cursor [0]], tyOffsets [cursor [0]]);

	choice = cursor [cursor [0]];
    }


    public static void drawCursor (int x, int y)
    {
	int[] xPoints = {x, x, x + 14};
	int[] yPoints = {y, y + 14, y + 7};
	c.setColor (cursorColor);
	c.fillPolygon (xPoints, yPoints, 3);
	c.setColor (Color.black);
	c.drawPolygon (xPoints, yPoints, 3);
    }


    public static void eraseCursor (int x, int y, Color erase)
    {
	c.setColor (erase);
	c.fillRect (x, y, 15, 15);
	c.setColor (Color.black);
    }


    public static void pause ()
	// ^ invoked when the ctrlStart / Pause button is pressed in-game.
    {
	tx = 250;
	ty = 130;
	c.setColor (Color.black);
	c.fillRect (0, 0, consoleWidth, consoleHeight);
	c.setFont (fontTitle);
	c.setColor (Color.orange);
	c.drawString ("PAUSED", tx, ty);

	c.setFont (fontMenuPlain);
	c.setColor (Color.white);
	tx = 80;
	ty = 250;
	c.drawString ("Continue", tx, ty);
	ty += ln1;
	c.drawString ("Cube Menu", tx, ty);
	ty += ln1;
	c.drawString ("Title Menu", tx, ty);
	ty += ln1;
	c.drawString ("Quit", tx, ty);

	cursor [0] = 3;
	cursor [3] = 0;
	tyOffsets [3] = cursor [3] * ln1 + 238;
	drawCursor (txOffsets [3], tyOffsets [3]);
	do
	    navigate (ln1, 238, Color.black);

	while (input != ctrlStart
		&& input != ctrlGbaA
		&& input != ctrlGbaB);

	if (input != ctrlGbaB)
	{
	    if (choice == 3)
	    {
		isGaming = false;
		isRunning = false;
	    }
	    else if (choice == 2)
	    {
		isGaming = false;
	    }
	    else if (choice == 1)
	    {
		isGaming = false;
		skipToSelection = true;
	    }
	}
	input = nul;
    }


    public static void initializeOneCube ()
	// ^ assigns the starting coordinates of all of
	//   the vertices that make up the 1x1x1 cube
    {
	// A larger z-coordinate means that the
	// vertex is further away from the user.

	centerRed = new double[] []
	{
	    { // 0 - This is the point at the very centre of the face.
		// This is used for calculations concerning
		// rotations around an arbitrary axis.
		// i.e., this is the "CONTROL".

		0, 00000, 00000, -r [0], 0, 0, 0
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1
	    ,
	    { // 1
		0, r [0], r [0], -r [0], 0, 0, 0
	    }
	    ,
	    { // 2
		0, -r [0], r [0], -r [0], 0, 0, 0
	    }
	    ,
	    { // 3
		0, -r [0], -r [0], -r [0], 0, 0, 0
	    }
	    ,
	    { // 4
		0, r [0], -r [0], -r [0], 0, 0, 0
	    }
	}
	;
	centerYellow = new double[] []
	{

	    { // 0 - CONTROL
		1, 00000, -r [0], 00000, 0, 0, 0
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1
	    ,
	    { // 1
		1, r [0], -r [0], r [0], 0, 0, 0
	    }
	    ,
	    { // 2
		1, -r [0], -r [0], r [0], 0, 0, 0
	    }
	    ,
	    { // 3
		1, -r [0], -r [0], -r [0], 0, 0, 0
	    }
	    ,
	    { // 4
		1, r [0], -r [0], -r [0], 0, 0, 0
	    }
	}
	;
	centerBlue = new double[] []
	{
	    { // 0 - CONTROL
		2, r [0], 00000, 00000, 0, 0, 0,
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1
	    ,
	    { // 1
		2, r [0], r [0], r [0], 0, 0, 0
	    }
	    ,

	    { // 2
		2, r [0], r [0], -r [0], 0, 0, 0
	    }
	    ,

	    { // 3
		2, r [0], -r [0], -r [0], 0, 0, 0
	    }
	    ,

	    { // 4
		2, r [0], -r [0], r [0], 0, 0, 0
	    }
	}
	;
	centerWhite = new double[] []
	{
	    { // 0 - CONTROL
		3, 00000, r [0], 00000, 0, 0, 0
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1
	    ,
	    { // 1
		3, r [0], r [0], r [0], 0, 0, 0
	    }
	    ,
	    { // 2
		3, -r [0], r [0], r [0], 0, 0, 0
	    }
	    ,
	    { // 3
		3, -r [0], r [0], -r [0], 0, 0, 0
	    }
	    ,
	    { // 4
		3, r [0], r [0], -r [0], 0, 0, 0
	    }
	}
	;
	centerGreen = new double[] []
	{
	    { // 0 - CONTROL
		4, -r [0], 00000, 00000, 0, 0, 0
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1
	    ,
	    { // 1
		4, -r [0], r [0], r [0], 0, 0, 0
	    }
	    ,

	    { // 2
		4, -r [0], r [0], -r [0], 0, 0, 0
	    }
	    ,

	    { // 3
		4, -r [0], -r [0], -r [0], 0, 0, 0
	    }
	    ,

	    { // 4
		4, -r [0], -r [0], r [0], 0, 0, 0
	    }
	}
	;
	centerOrange = new double[] []
	{
	    { // 0 - CONTROL
		5, 00000, 00000, r [0], 0, 0, 0
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1
	    ,
	    { // 1
		5, r [0], r [0], r [0], 0, 0, 0
	    }
	    ,
	    { // 2
		5, -r [0], r [0], r [0], 0, 0, 0
	    }
	    ,
	    { // 3
		5, -r [0], -r [0], r [0], 0, 0, 0
	    }
	    ,
	    { // 4
		5, r [0], -r [0], r [0], 0, 0, 0
	    }
	}
	;

    } // initializeOneCube method


    public static void initializeTwoCube ()
	// ^ assigns the starting coordinates of all of
	//  the vertices that make up the 2 x 2 x 2 cube
    {
	// A larger z-coordinate means that the
	// vertex is further away from the user.


	// CENTERS

	centerRed = new double[] []
	{
	    { // 0 - CONTROL
		0, 00000, 00000, -r [0], 0, 0, 0
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1
	}
	;
	centerYellow = new double[] []
	{
	    { // 0 - CONTROL
		1, 00000, -r [0], 00000, 0, 0, 0
	    }
	}
	;
	centerBlue = new double[] []
	{
	    { // 0 - CONTROL
		2, r [0], 00000, 00000, 0, 0, 0
	    }
	}
	;
	centerWhite = new double[] []
	{
	    { // 0 - CONTROL
		3, 00000, r [0], 00000, 0, 0, 0
	    }
	}
	;
	centerGreen = new double[] []
	{
	    { // 0 - CONTROL
		4, -r [0], 00000, 00000, 0, 0, 0
	    }
	}
	;
	centerOrange = new double[] []
	{
	    { // 0 - CONTROL
		5, 00000, 00000, r [0], 0, 0, 0
	    }
	}
	;

	// CORNERS

	cornerRedBlueWhite = new double[] []
	{
	    { // 0 (Red)
		0, r [0], r [0], -r [0], 0, 0, 0, 23
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1, corner identifier
	    ,
	    { // 1
		0, 00000, r [0], -r [0], 0, 0, 0
	    }
	    ,
	    { // 2
		0, 00000, 00000, -r [0], 0, 0, 0
	    }
	    ,
	    { // 3
		0, r [0], 00000, -r [0], 0, 0, 0
	    }
	    ,
	    { // 4 (Blue)
		2, r [0], r [0], 00000, 0, 0, 0
	    }
	    ,
	    { // 5
		2, r [0], r [0], -r [0], 0, 0, 0
	    }
	    ,
	    { // 6
		2, r [0], 00000, -r [0], 0, 0, 0
	    }
	    ,
	    { // 7
		2, r [0], 00000, 00000, 0, 0, 0
	    }
	    ,
	    { // 8 (White)
		3, r [0], r [0], 00000, 0, 0, 0
	    }
	    ,
	    { // 9
		3, 00000, r [0], 00000, 0, 0, 0
	    }
	    ,
	    { // 10
		3, 00000, r [0], -r [0], 0, 0, 0
	    }
	    ,
	    { // 11
		3, r [0], r [0], -r [0], 0, 0, 0
	    }
	}
	;

	cornerRedYellowBlue = new double[] []
	{
	    { // 0 (Red)
		0, r [0], 00000, -r [0], 0, 0, 0, 12
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1, corner identifier
	    ,
	    { // 1
		0, 00000, 00000, -r [0], 0, 0, 0
	    }
	    ,
	    { // 2
		0, 00000, -r [0], -r [0], 0, 0, 0
	    }
	    ,
	    { // 3
		0, r [0], -r [0], -r [0], 0, 0, 0
	    }
	    ,
	    { // 4 (Yellow)
		1, r [0], -r [0], 00000, 0, 0, 0
	    }
	    ,
	    { // 5
		1, 00000, -r [0], 00000, 0, 0, 0
	    }
	    ,
	    { // 6
		1, 00000, -r [0], -r [0], 0, 0, 0
	    }
	    ,
	    { // 7
		1, r [0], -r [0], -r [0], 0, 0, 0
	    }
	    ,
	    { // 8 (Blue)
		2, r [0], 00000, 00000, 0, 0, 0
	    }
	    ,
	    { // 9
		2, r [0], 00000, -r [0], 0, 0, 0
	    }
	    ,
	    { // 10
		2, r [0], -r [0], -r [0], 0, 0, 0
	    }
	    ,
	    { // 11
		2, r [0], -r [0], 00000, 0, 0, 0
	    }
	}
	;

	cornerRedYellowGreen = new double[] []
	{
	    { // 0 (Red)
		0, 00000, 00000, -r [0], 0, 0, 0, 14
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1, corner identifier
	    ,
	    { // 1
		0, -r [0], 00000, -r [0], 0, 0, 0
	    }
	    ,
	    { // 2
		0, -r [0], -r [0], -r [0], 0, 0, 0
	    }
	    ,
	    { // 3
		0, 00000, -r [0], -r [0], 0, 0, 0
	    }
	    ,
	    { // 4 (Yellow)
		1, 00000, -r [0], 00000, 0, 0, 0
	    }
	    ,
	    { // 5
		1, -r [0], -r [0], 00000, 0, 0, 0
	    }
	    ,
	    { // 6
		1, -r [0], -r [0], -r [0], 0, 0, 0
	    }
	    ,
	    { // 7
		1, 00000, -r [0], -r [0], 0, 0, 0
	    }
	    ,
	    { // 8 (Green)
		4, -r [0], 00000, 00000, 0, 0, 0
	    }
	    ,
	    { // 9
		4, -r [0], 00000, -r [0], 0, 0, 0
	    }
	    ,
	    { // 10
		4, -r [0], -r [0], -r [0], 0, 0, 0
	    }
	    ,
	    { // 11
		4, -r [0], -r [0], 00000, 0, 0, 0
	    }
	}
	;

	cornerRedWhiteGreen = new double[] []
	{
	    { // 0 (Red)
		0, 00000, r [0], -r [0], 0, 0, 0, 34
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1, corner identifier
	    ,
	    { // 1
		0, -r [0], r [0], -r [0], 0, 0, 0
	    }
	    ,
	    { // 2
		0, -r [0], 00000, -r [0], 0, 0, 0
	    }
	    ,
	    { // 3
		0, 00000, 00000, -r [0], 0, 0, 0
	    }
	    ,
	    { // 4 (White)
		3, 00000, r [0], 00000, 0, 0, 0
	    }
	    ,
	    { // 5
		3, -r [0], r [0], 00000, 0, 0, 0
	    }
	    ,
	    { // 6
		3, -r [0], r [0], -r [0], 0, 0, 0
	    }
	    ,
	    { // 7
		3, 00000, r [0], -r [0], 0, 0, 0
	    }
	    ,
	    { // 8 (Green)
		4, -r [0], r [0], 00000, 0, 0, 0
	    }
	    ,
	    { // 9
		4, -r [0], r [0], -r [0], 0, 0, 0
	    }
	    ,
	    { // 10
		4, -r [0], 00000, -r [0], 0, 0, 0
	    }
	    ,
	    { // 11
		4, -r [0], 00000, 00000, 0, 0, 0
	    }
	}
	;

	cornerBlueWhiteOrange = new double[] []
	{
	    { // 0 (Blue)
		2, r [0], r [0], r [0], 0, 0, 0, 235
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1, corner identifier
	    ,
	    { // 1
		2, r [0], r [0], 00000, 0, 0, 0
	    }
	    ,
	    { // 2
		2, r [0], 00000, 00000, 0, 0, 0
	    }
	    ,
	    { // 3
		2, r [0], 00000, r [0], 0, 0, 0
	    }
	    ,
	    { // 4 (White)
		3, r [0], r [0], r [0], 0, 0, 0
	    }
	    ,
	    { // 5
		3, 00000, r [0], r [0], 0, 0, 0
	    }
	    ,
	    { // 6
		3, 00000, r [0], 00000, 0, 0, 0
	    }
	    ,
	    { // 7
		3, r [0], r [0], 00000, 0, 0, 0
	    }
	    ,
	    { // 8 (Orange)
		5, r [0], r [0], r [0], 0, 0, 0
	    }
	    ,
	    { // 9
		5, 00000, r [0], r [0], 0, 0, 0
	    }
	    ,
	    { // 10
		5, 00000, 00000, r [0], 0, 0, 0
	    }
	    ,
	    { // 11
		5, r [0], 00000, r [0], 0, 0, 0
	    }
	}
	;

	cornerYellowBlueOrange = new double[] []
	{
	    { // 0 (Yellow)
		1, r [0], -r [0], r [0], 0, 0, 0, 125
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1, corner identifier
	    ,
	    { // 1
		1, 00000, -r [0], r [0], 0, 0, 0
	    }
	    ,
	    { // 2
		1, 00000, -r [0], 00000, 0, 0, 0
	    }
	    ,
	    { // 3
		1, r [0], -r [0], 00000, 0, 0, 0
	    }
	    ,
	    { // 4 (Blue)
		2, r [0], 00000, r [0], 0, 0, 0
	    }
	    ,
	    { // 5
		2, r [0], 00000, 00000, 0, 0, 0
	    }
	    ,
	    { // 6
		2, r [0], -r [0], 00000, 0, 0, 0
	    }
	    ,
	    { // 7
		2, r [0], -r [0], r [0], 0, 0, 0
	    }
	    ,
	    { // 8 (Orange)
		5, r [0], 00000, r [0], 0, 0, 0
	    }
	    ,
	    { // 9
		5, 00000, 00000, r [0], 0, 0, 0
	    }
	    ,
	    { // 10
		5, 00000, -r [0], r [0], 0, 0, 0
	    }
	    ,
	    { // 11
		5, r [0], -r [0], r [0], 0, 0, 0
	    }
	}
	;

	cornerYellowGreenOrange = new double[] []
	{
	    { // 0 (Yellow)
		1, 00000, -r [0], r [0], 0, 0, 0, 145
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1, corner identifier
	    ,
	    { // 1
		1, -r [0], -r [0], r [0], 0, 0, 0
	    }
	    ,
	    { // 2
		1, -r [0], -r [0], 00000, 0, 0, 0
	    }
	    ,
	    { // 3
		1, 00000, -r [0], 00000, 0, 0, 0
	    }
	    ,
	    { // 4 (Green)
		4, -r [0], 00000, 00000, 0, 0, 0
	    }
	    ,
	    { // 5
		4, -r [0], 00000, r [0], 0, 0, 0
	    }
	    ,
	    { // 6
		4, -r [0], -r [0], r [0], 0, 0, 0
	    }
	    ,
	    { // 7
		4, -r [0], -r [0], 00000, 0, 0, 0
	    }
	    ,
	    { // 8 (Orange)
		5, 00000, 00000, r [0], 0, 0, 0
	    }
	    ,
	    { // 9
		5, -r [0], 00000, r [0], 0, 0, 0
	    }
	    ,
	    { // 10
		5, -r [0], -r [0], r [0], 0, 0, 0
	    }
	    ,
	    { // 11
		5, 00000, -r [0], r [0], 0, 0, 0
	    }
	}
	;

	cornerWhiteGreenOrange = new double[] []
	{
	    { // 0 (White)
		3, 00000, r [0], r [0], 0, 0, 0, 345
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1, corner identifier
	    ,
	    { // 1
		3, -r [0], r [0], r [0], 0, 0, 0
	    }
	    ,
	    { // 2
		3, -r [0], r [0], 00000, 0, 0, 0
	    }
	    ,
	    { // 3
		3, 00000, r [0], 00000, 0, 0, 0
	    }
	    ,
	    { // 4 (Green)
		4, -r [0], r [0], 00000, 0, 0, 0
	    }
	    ,
	    { // 5
		4, -r [0], r [0], r [0], 0, 0, 0
	    }
	    ,
	    { // 6
		4, -r [0], 00000, r [0], 0, 0, 0
	    }
	    ,
	    { // 7
		4, -r [0], 00000, 00000, 0, 0, 0
	    }
	    ,
	    { // 8 (Orange)
		5, 00000, r [0], r [0], 0, 0, 0
	    }
	    ,
	    { // 9
		5, -r [0], r [0], r [0], 0, 0, 0
	    }
	    ,
	    { // 10
		5, -r [0], 00000, r [0], 0, 0, 0
	    }
	    ,
	    { // 11
		5, 00000, 00000, r [0], 0, 0, 0
	    }
	}
	;

	// CENTER OWNERSHIP

	centerOwnership = new int[] []
	{
	    { // red (0)
		23, 12, 14, 34
	    }
	    ,
	    { // yellow (1)
		12, 14, 125, 145
	    }
	    ,
	    { // blue (2)
		23, 12, 235, 125
	    }
	    ,
	    { // white (3)
		23, 34, 235, 345
	    }
	    ,
	    { // green (4)
		14, 34, 145, 345
	    }
	    ,
	    { // orange (5)
		235, 125, 145, 345
	    }
	}
	;
	tempCenterOwnership = new int [centerOwnership.length] [centerOwnership [0].length];

    } // initializeTwoCube method


    public static void initializeThreeCube ()
	// ^ assigns the starting coordinates of all of
	//  the vertices that make up the 3 x 3 x 3 cube
    {
	// A larger z-coordinate means that the
	// vertex is further away from the user.


	// CENTERS

	centerRed = new double[] []
	{
	    { // 0 - This is the point at the very centre of the face.
		// This is used for calculations concerning
		// rotations around an arbitrary axis.
		// i.e., this is the "CONTROL".

		0, 00000, 00000, -r [2], 0, 0, 0
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1
	    ,
	    { // 1
		0, r [1], r [1], -r [2], 0, 0, 0
	    }
	    ,
	    { // 2
		0, -r [1], r [1], -r [2], 0, 0, 0
	    }
	    ,
	    { // 3
		0, -r [1], -r [1], -r [2], 0, 0, 0
	    }
	    ,
	    { // 4
		0, r [1], -r [1], -r [2], 0, 0, 0
	    }
	}
	;
	centerYellow = new double[] []
	{

	    { // 0 - CONTROL
		1, 00000, -r [2], 00000, 0, 0, 0
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1
	    ,
	    { // 1
		1, r [1], -r [2], r [1], 0, 0, 0
	    }
	    ,
	    { // 2
		1, -r [1], -r [2], r [1], 0, 0, 0
	    }
	    ,
	    { // 3
		1, -r [1], -r [2], -r [1], 0, 0, 0
	    }
	    ,
	    { // 4
		1, r [1], -r [2], -r [1], 0, 0, 0
	    }
	}
	;
	centerBlue = new double[] []
	{
	    { // 0 - CONTROL
		2, r [2], 00000, 00000, 0, 0, 0,
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1
	    ,
	    { // 1
		2, r [2], r [1], r [1], 0, 0, 0
	    }
	    ,

	    { // 2
		2, r [2], r [1], -r [1], 0, 0, 0
	    }
	    ,

	    { // 3
		2, r [2], -r [1], -r [1], 0, 0, 0
	    }
	    ,

	    { // 4
		2, r [2], -r [1], r [1], 0, 0, 0
	    }
	}
	;
	centerWhite = new double[] []
	{
	    { // 0 - CONTROL
		3, 00000, r [2], 00000, 0, 0, 0
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1
	    ,
	    { // 1
		3, r [1], r [2], r [1], 0, 0, 0
	    }
	    ,
	    { // 2
		3, -r [1], r [2], r [1], 0, 0, 0
	    }
	    ,
	    { // 3
		3, -r [1], r [2], -r [1], 0, 0, 0
	    }
	    ,
	    { // 4
		3, r [1], r [2], -r [1], 0, 0, 0
	    }
	}
	;
	centerGreen = new double[] []
	{
	    { // 0 - CONTROL
		4, -r [2], 00000, 00000, 0, 0, 0
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1
	    ,
	    { // 1
		4, -r [2], r [1], r [1], 0, 0, 0
	    }
	    ,

	    { // 2
		4, -r [2], r [1], -r [1], 0, 0, 0
	    }
	    ,

	    { // 3
		4, -r [2], -r [1], -r [1], 0, 0, 0
	    }
	    ,

	    { // 4
		4, -r [2], -r [1], r [1], 0, 0, 0
	    }
	}
	;
	centerOrange = new double[] []
	{
	    { // 0 - CONTROL
		5, 00000, 00000, r [2], 0, 0, 0
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1
	    ,
	    { // 1
		5, r [1], r [1], r [2], 0, 0, 0
	    }
	    ,
	    { // 2
		5, -r [1], r [1], r [2], 0, 0, 0
	    }
	    ,
	    { // 3
		5, -r [1], -r [1], r [2], 0, 0, 0
	    }
	    ,
	    { // 4
		5, r [1], -r [1], r [2], 0, 0, 0
	    }
	}
	;

	// CORNERS

	cornerRedBlueWhite = new double[] []
	{
	    { // 0 (Red)
		0, r [2], r [2], -r [2], 0, 0, 0, 23
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1, corner identifier
	    ,
	    { // 1
		0, r [1], r [2], -r [2], 0, 0, 0
	    }
	    ,
	    { // 2
		0, r [1], r [1], -r [2], 0, 0, 0
	    }
	    ,
	    { // 3
		0, r [2], r [1], -r [2], 0, 0, 0
	    }
	    ,
	    { // 4 (Blue)
		2, r [2], r [2], -r [1], 0, 0, 0
	    }
	    ,
	    { // 5
		2, r [2], r [2], -r [2], 0, 0, 0
	    }
	    ,
	    { // 6
		2, r [2], r [1], -r [2], 0, 0, 0
	    }
	    ,
	    { // 7
		2, r [2], r [1], -r [1], 0, 0, 0
	    }
	    ,
	    { // 8 (White)
		3, r [2], r [2], -r [1], 0, 0, 0
	    }
	    ,
	    { // 9
		3, r [1], r [2], -r [1], 0, 0, 0
	    }
	    ,
	    { // 10
		3, r [1], r [2], -r [2], 0, 0, 0
	    }
	    ,
	    { // 11
		3, r [2], r [2], -r [2], 0, 0, 0
	    }
	}
	;

	cornerRedYellowBlue = new double[] []
	{
	    { // 0 (Red)
		0, r [2], -r [1], -r [2], 0, 0, 0, 12
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1, corner identifier
	    ,
	    { // 1
		0, r [1], -r [1], -r [2], 0, 0, 0
	    }
	    ,
	    { // 2
		0, r [1], -r [2], -r [2], 0, 0, 0
	    }
	    ,
	    { // 3
		0, r [2], -r [2], -r [2], 0, 0, 0
	    }
	    ,
	    { // 4 (Yellow)
		1, r [2], -r [2], -r [1], 0, 0, 0
	    }
	    ,
	    { // 5
		1, r [1], -r [2], -r [1], 0, 0, 0
	    }
	    ,
	    { // 6
		1, r [1], -r [2], -r [2], 0, 0, 0
	    }
	    ,
	    { // 7
		1, r [2], -r [2], -r [2], 0, 0, 0
	    }
	    ,
	    { // 8 (Blue)
		2, r [2], -r [1], -r [1], 0, 0, 0
	    }
	    ,
	    { // 9
		2, r [2], -r [1], -r [2], 0, 0, 0
	    }
	    ,
	    { // 10
		2, r [2], -r [2], -r [2], 0, 0, 0
	    }
	    ,
	    { // 11
		2, r [2], -r [2], -r [1], 0, 0, 0
	    }
	}
	;

	cornerRedYellowGreen = new double[] []
	{
	    { // 0 (Red)
		0, -r [1], -r [1], -r [2], 0, 0, 0, 14
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1, corner identifier
	    ,
	    { // 1
		0, -r [2], -r [1], -r [2], 0, 0, 0
	    }
	    ,
	    { // 2
		0, -r [2], -r [2], -r [2], 0, 0, 0
	    }
	    ,
	    { // 3
		0, -r [1], -r [2], -r [2], 0, 0, 0
	    }
	    ,
	    { // 4 (Yellow)
		1, -r [1], -r [2], -r [1], 0, 0, 0
	    }
	    ,
	    { // 5
		1, -r [2], -r [2], -r [1], 0, 0, 0
	    }
	    ,
	    { // 6
		1, -r [2], -r [2], -r [2], 0, 0, 0
	    }
	    ,
	    { // 7
		1, -r [1], -r [2], -r [2], 0, 0, 0
	    }
	    ,
	    { // 8 (Green)
		4, -r [2], -r [1], -r [1], 0, 0, 0
	    }
	    ,
	    { // 9
		4, -r [2], -r [1], -r [2], 0, 0, 0
	    }
	    ,
	    { // 10
		4, -r [2], -r [2], -r [2], 0, 0, 0
	    }
	    ,
	    { // 11
		4, -r [2], -r [2], -r [1], 0, 0, 0
	    }
	}
	;

	cornerRedWhiteGreen = new double[] []
	{
	    { // 0 (Red)
		0, -r [1], r [2], -r [2], 0, 0, 0, 34
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1, corner identifier
	    ,
	    { // 1
		0, -r [2], r [2], -r [2], 0, 0, 0
	    }
	    ,
	    { // 2
		0, -r [2], r [1], -r [2], 0, 0, 0
	    }
	    ,
	    { // 3
		0, -r [1], r [1], -r [2], 0, 0, 0
	    }
	    ,
	    { // 4 (White)
		3, -r [1], r [2], -r [1], 0, 0, 0
	    }
	    ,
	    { // 5
		3, -r [2], r [2], -r [1], 0, 0, 0
	    }
	    ,
	    { // 6
		3, -r [2], r [2], -r [2], 0, 0, 0
	    }
	    ,
	    { // 7
		3, -r [1], r [2], -r [2], 0, 0, 0
	    }
	    ,
	    { // 8 (Green)
		4, -r [2], r [2], -r [1], 0, 0, 0
	    }
	    ,
	    { // 9
		4, -r [2], r [2], -r [2], 0, 0, 0
	    }
	    ,
	    { // 10
		4, -r [2], r [1], -r [2], 0, 0, 0
	    }
	    ,
	    { // 11
		4, -r [2], r [1], -r [1], 0, 0, 0
	    }
	}
	;

	cornerBlueWhiteOrange = new double[] []
	{
	    { // 0 (Blue)
		2, r [2], r [2], r [2], 0, 0, 0, 235
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1, corner identifier
	    ,
	    { // 1
		2, r [2], r [2], r [1], 0, 0, 0
	    }
	    ,
	    { // 2
		2, r [2], r [1], r [1], 0, 0, 0
	    }
	    ,
	    { // 3
		2, r [2], r [1], r [2], 0, 0, 0
	    }
	    ,
	    { // 4 (White)
		3, r [2], r [2], r [2], 0, 0, 0
	    }
	    ,
	    { // 5
		3, r [1], r [2], r [2], 0, 0, 0
	    }
	    ,
	    { // 6
		3, r [1], r [2], r [1], 0, 0, 0
	    }
	    ,
	    { // 7
		3, r [2], r [2], r [1], 0, 0, 0
	    }
	    ,
	    { // 8 (Orange)
		5, r [2], r [2], r [2], 0, 0, 0
	    }
	    ,
	    { // 9
		5, r [1], r [2], r [2], 0, 0, 0
	    }
	    ,
	    { // 10
		5, r [1], r [1], r [2], 0, 0, 0
	    }
	    ,
	    { // 11
		5, r [2], r [1], r [2], 0, 0, 0
	    }
	}
	;

	cornerYellowBlueOrange = new double[] []
	{
	    { // 0 (Yellow)
		1, r [2], -r [2], r [2], 0, 0, 0, 125
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1, corner identifier
	    ,
	    { // 1
		1, r [1], -r [2], r [2], 0, 0, 0
	    }
	    ,
	    { // 2
		1, r [1], -r [2], r [1], 0, 0, 0
	    }
	    ,
	    { // 3
		1, r [2], -r [2], r [1], 0, 0, 0
	    }
	    ,
	    { // 4 (Blue)
		2, r [2], -r [1], r [2], 0, 0, 0
	    }
	    ,
	    { // 5
		2, r [2], -r [1], r [1], 0, 0, 0
	    }
	    ,
	    { // 6
		2, r [2], -r [2], r [1], 0, 0, 0
	    }
	    ,
	    { // 7
		2, r [2], -r [2], r [2], 0, 0, 0
	    }
	    ,
	    { // 8 (Orange)
		5, r [2], -r [1], r [2], 0, 0, 0
	    }
	    ,
	    { // 9
		5, r [1], -r [1], r [2], 0, 0, 0
	    }
	    ,
	    { // 10
		5, r [1], -r [2], r [2], 0, 0, 0
	    }
	    ,
	    { // 11
		5, r [2], -r [2], r [2], 0, 0, 0
	    }
	}
	;

	cornerYellowGreenOrange = new double[] []
	{
	    { // 0 (Yellow)
		1, -r [1], -r [2], r [2], 0, 0, 0, 145
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1, corner identifier
	    ,
	    { // 1
		1, -r [2], -r [2], r [2], 0, 0, 0
	    }
	    ,
	    { // 2
		1, -r [2], -r [2], r [1], 0, 0, 0
	    }
	    ,
	    { // 3
		1, -r [1], -r [2], r [1], 0, 0, 0
	    }
	    ,
	    { // 4 (Green)
		4, -r [2], -r [1], r [1], 0, 0, 0
	    }
	    ,
	    { // 5
		4, -r [2], -r [1], r [2], 0, 0, 0
	    }
	    ,
	    { // 6
		4, -r [2], -r [2], r [2], 0, 0, 0
	    }
	    ,
	    { // 7
		4, -r [2], -r [2], r [1], 0, 0, 0
	    }
	    ,
	    { // 8 (Orange)
		5, -r [1], -r [1], r [2], 0, 0, 0
	    }
	    ,
	    { // 9
		5, -r [2], -r [1], r [2], 0, 0, 0
	    }
	    ,
	    { // 10
		5, -r [2], -r [2], r [2], 0, 0, 0
	    }
	    ,
	    { // 11
		5, -r [1], -r [2], r [2], 0, 0, 0
	    }
	}
	;

	cornerWhiteGreenOrange = new double[] []
	{
	    { // 0 (White)
		3, -r [1], r [2], r [2], 0, 0, 0, 345
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1, corner identifier
	    ,
	    { // 1
		3, -r [2], r [2], r [2], 0, 0, 0
	    }
	    ,
	    { // 2
		3, -r [2], r [2], r [1], 0, 0, 0
	    }
	    ,
	    { // 3
		3, -r [1], r [2], r [1], 0, 0, 0
	    }
	    ,
	    { // 4 (Green)
		4, -r [2], r [2], r [1], 0, 0, 0
	    }
	    ,
	    { // 5
		4, -r [2], r [2], r [2], 0, 0, 0
	    }
	    ,
	    { // 6
		4, -r [2], r [1], r [2], 0, 0, 0
	    }
	    ,
	    { // 7
		4, -r [2], r [1], r [1], 0, 0, 0
	    }
	    ,
	    { // 8 (Orange)
		5, -r [1], r [2], r [2], 0, 0, 0
	    }
	    ,
	    { // 9
		5, -r [2], r [2], r [2], 0, 0, 0
	    }
	    ,
	    { // 10
		5, -r [2], r [1], r [2], 0, 0, 0
	    }
	    ,
	    { // 11
		5, -r [1], r [1], r [2], 0, 0, 0
	    }
	}
	;

	// EDGES

	edgeRedWhite = new double[] []
	{
	    { // 0 (Red)
		0, r [1], r [2], -r [2], 0, 0, 0, 3
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1, edge identifier
	    ,
	    { // 1
		0, -r [1], r [2], -r [2], 0, 0, 0
	    }
	    ,
	    { // 2
		0, -r [1], r [1], -r [2], 0, 0, 0
	    }
	    ,
	    { // 3
		0, r [1], r [1], -r [2], 0, 0, 0
	    }
	    ,
	    { // 4 (White)
		3, r [1], r [2], -r [1], 0, 0, 0
	    }
	    ,
	    { // 5
		3, -r [1], r [2], -r [1], 0, 0, 0
	    }
	    ,
	    { // 6
		3, -r [1], r [2], -r [2], 0, 0, 0
	    }
	    ,
	    { // 7
		3, r [1], r [2], -r [2], 0, 0, 0
	    }
	}
	;

	edgeRedBlue = new double[] []
	{
	    { // 0 (Red)
		0, r [2], r [1], -r [2], 0, 0, 0, 2
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1, edge identifier
	    ,
	    { // 1
		0, r [1], r [1], -r [2], 0, 0, 0
	    }
	    ,
	    { // 2
		0, r [1], -r [1], -r [2], 0, 0, 0
	    }
	    ,
	    { // 3
		0, r [2], -r [1], -r [2], 0, 0, 0
	    }
	    ,
	    { // 4 (Blue)
		2, r [2], r [1], -r [1], 0, 0, 0
	    }
	    ,
	    { // 5
		2, r [2], r [1], -r [2], 0, 0, 0
	    }
	    ,
	    { // 6
		2, r [2], -r [1], -r [2], 0, 0, 0
	    }
	    ,
	    { // 7
		2, r [2], -r [1], -r [1], 0, 0, 0
	    }
	}
	;

	edgeRedYellow = new double[] []
	{
	    { // 0 (Red)
		0, r [1], -r [1], -r [2], 0, 0, 0, 1
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1, edge identifier
	    ,
	    { // 1
		0, -r [1], -r [1], -r [2], 0, 0, 0
	    }
	    ,
	    { // 2
		0, -r [1], -r [2], -r [2], 0, 0, 0
	    }
	    ,
	    { // 3
		0, r [1], -r [2], -r [2], 0, 0, 0
	    }
	    ,
	    { // 4 (Yellow)
		1, r [1], -r [2], -r [1], 0, 0, 0
	    }
	    ,
	    { // 5
		1, -r [1], -r [2], -r [1], 0, 0, 0
	    }
	    ,
	    { // 6
		1, -r [1], -r [2], -r [2], 0, 0, 0
	    }
	    ,
	    { // 7
		1, r [1], -r [2], -r [2], 0, 0, 0
	    }
	}
	;

	edgeRedGreen = new double[] []
	{
	    { // 0 (Red)
		0, -r [1], r [1], -r [2], 0, 0, 0, 4
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1, edge identifier
	    ,
	    { // 1
		0, -r [2], r [1], -r [2], 0, 0, 0
	    }
	    ,
	    { // 2
		0, -r [2], -r [1], -r [2], 0, 0, 0
	    }
	    ,
	    { // 3
		0, -r [1], -r [1], -r [2], 0, 0, 0
	    }
	    ,
	    { // 4 (Green)
		4, -r [2], r [1], -r [1], 0, 0, 0
	    }
	    ,
	    { // 5
		4, -r [2], r [1], -r [2], 0, 0, 0
	    }
	    ,
	    { // 6
		4, -r [2], -r [1], -r [2], 0, 0, 0
	    }
	    ,
	    { // 7
		4, -r [2], -r [1], -r [1], 0, 0, 0
	    }
	}
	;

	edgeBlueWhite = new double[] []
	{
	    { // 0 (Blue)
		2, r [2], r [2], r [1], 0, 0, 0, 23
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1, edge identifier
	    ,
	    { // 1
		2, r [2], r [2], -r [1], 0, 0, 0
	    }
	    ,
	    { // 2
		2, r [2], r [1], -r [1], 0, 0, 0
	    }
	    ,
	    { // 3
		2, r [2], r [1], r [1], 0, 0, 0
	    }
	    ,
	    { // 4 (White)
		3, r [2], r [2], r [1], 0, 0, 0
	    }
	    ,
	    { // 5
		3, r [1], r [2], r [1], 0, 0, 0
	    }
	    ,
	    { // 6
		3, r [1], r [2], -r [1], 0, 0, 0
	    }
	    ,
	    { // 7
		3, r [2], r [2], -r [1], 0, 0, 0
	    }
	}
	;

	edgeYellowBlue = new double[] []
	{
	    { // 0 (Yellow)
		1, r [2], -r [2], r [1], 0, 0, 0, 12
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1, edge identifier
	    ,
	    { // 1
		1, r [1], -r [2], r [1], 0, 0, 0
	    }
	    ,
	    { // 2
		1, r [1], -r [2], -r [1], 0, 0, 0
	    }
	    ,
	    { // 3
		1, r [2], -r [2], -r [1], 0, 0, 0
	    }
	    ,
	    { // 4 (Blue)
		2, r [2], -r [1], r [1], 0, 0, 0
	    }
	    ,
	    { // 5
		2, r [2], -r [1], -r [1], 0, 0, 0
	    }
	    ,
	    { // 6
		2, r [2], -r [2], -r [1], 0, 0, 0
	    }
	    ,
	    { // 7
		2, r [2], -r [2], r [1], 0, 0, 0
	    }
	}
	;

	edgeYellowGreen = new double[] []
	{
	    { // 0 (Yellow)
		1, -r [1], -r [2], r [1], 0, 0, 0, 14
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1, edge identifier
	    ,
	    { // 1
		1, -r [2], -r [2], r [1], 0, 0, 0
	    }
	    ,
	    { // 2
		1, -r [2], -r [2], -r [1], 0, 0, 0
	    }
	    ,
	    { // 3
		1, -r [1], -r [2], -r [1], 0, 0, 0
	    }
	    ,
	    { // 4 (Green)
		4, -r [2], -r [1], r [1], 0, 0, 0
	    }
	    ,
	    { // 5
		4, -r [2], -r [1], -r [1], 0, 0, 0
	    }
	    ,
	    { // 6
		4, -r [2], -r [2], -r [1], 0, 0, 0
	    }
	    ,
	    { // 7
		4, -r [2], -r [2], r [1], 0, 0, 0
	    }
	}
	;

	edgeWhiteGreen = new double[] []
	{
	    { // 0 (White)
		3, -r [1], r [2], r [1], 0, 0, 0, 34
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1, edge identifier
	    ,
	    { // 1
		3, -r [2], r [2], r [1], 0, 0, 0
	    }
	    ,
	    { // 2
		3, -r [2], r [2], -r [1], 0, 0, 0
	    }
	    ,
	    { // 3
		3, -r [1], r [2], -r [1], 0, 0, 0
	    }
	    ,
	    { // 4 (Green)
		4, -r [2], r [2], r [1], 0, 0, 0
	    }
	    ,
	    { // 5
		4, -r [2], r [2], -r [1], 0, 0, 0
	    }
	    ,
	    { // 6
		4, -r [2], r [1], -r [1], 0, 0, 0
	    }
	    ,
	    { // 7
		4, -r [2], r [1], r [1], 0, 0, 0
	    }
	}
	;

	edgeWhiteOrange = new double[] []
	{
	    { // 0 (White)
		3, r [1], r [2], r [2], 0, 0, 0, 35
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1, edge identifier
	    ,
	    { // 1
		3, -r [1], r [2], r [2], 0, 0, 0
	    }
	    ,
	    { // 2
		3, -r [1], r [2], r [1], 0, 0, 0
	    }
	    ,
	    { // 3
		3, r [1], r [2], r [1], 0, 0, 0
	    }
	    ,
	    { // 4 (Orange)
		5, r [1], r [2], r [2], 0, 0, 0
	    }
	    ,
	    { // 5
		5, -r [1], r [2], r [2], 0, 0, 0
	    }
	    ,
	    { // 6
		5, -r [1], r [1], r [2], 0, 0, 0
	    }
	    ,
	    { // 7
		5, r [1], r [1], r [2], 0, 0, 0
	    }
	}
	;

	edgeBlueOrange = new double[] []
	{
	    { // 0 (Blue)
		2, r [2], r [1], r [2], 0, 0, 0, 25
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1, edge identifier
	    ,
	    { // 1
		2, r [2], r [1], r [1], 0, 0, 0
	    }
	    ,
	    { // 2
		2, r [2], -r [1], r [1], 0, 0, 0
	    }
	    ,
	    { // 3
		2, r [2], -r [1], r [2], 0, 0, 0
	    }
	    ,
	    { // 4 (Orange)
		5, r [2], r [1], r [2], 0, 0, 0
	    }
	    ,
	    { // 5
		5, r [1], r [1], r [2], 0, 0, 0
	    }
	    ,
	    { // 6
		5, r [1], -r [1], r [2], 0, 0, 0
	    }
	    ,
	    { // 7
		5, r [2], -r [1], r [2], 0, 0, 0
	    }
	}
	;

	edgeYellowOrange = new double[] []
	{
	    { // 0 (Yellow)
		1, r [1], -r [2], r [2], 0, 0, 0, 15
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1, edge identifier
	    ,
	    { // 1
		1, -r [1], -r [2], r [2], 0, 0, 0
	    }
	    ,
	    { // 2
		1, -r [1], -r [2], r [1], 0, 0, 0
	    }
	    ,
	    { // 3
		1, r [1], -r [2], r [1], 0, 0, 0
	    }
	    ,
	    { // 4 (Orange)
		5, r [1], -r [1], r [2], 0, 0, 0
	    }
	    ,
	    { // 5
		5, -r [1], -r [1], r [2], 0, 0, 0
	    }
	    ,
	    { // 6
		5, -r [1], -r [2], r [2], 0, 0, 0
	    }
	    ,
	    { // 7
		5, r [1], -r [2], r [2], 0, 0, 0
	    }
	}
	;

	edgeGreenOrange = new double[] []
	{
	    { // 0 (Green)
		4, -r [2], r [1], r [1], 0, 0, 0, 45
	    }
	    // ^ color, x0, y0, z0, x1, y1, z1, edge identifier
	    ,
	    { // 1
		4, -r [2], r [1], r [2], 0, 0, 0
	    }
	    ,
	    { // 2
		4, -r [2], -r [1], r [2], 0, 0, 0
	    }
	    ,
	    { // 3
		4, -r [2], -r [1], r [1], 0, 0, 0
	    }
	    ,
	    { // 4 (Orange)
		5, -r [1], r [1], r [2], 0, 0, 0
	    }
	    ,
	    { // 5
		5, -r [2], r [1], r [2], 0, 0, 0
	    }
	    ,
	    { // 6
		5, -r [2], -r [1], r [2], 0, 0, 0
	    }
	    ,
	    { // 7
		5, -r [1], -r [1], r [2], 0, 0, 0
	    }
	}
	;


	// CENTER OWNERSHIP

	centerOwnership = new int[] []
	{
	    { // red (0)
		23, 12, 14, 34, 3, 2, 1, 4
	    }
	    ,
	    { // yellow (1)
		12, 14, 125, 145, 1, 12, 15, 14
	    }
	    ,
	    { // blue (2)
		23, 12, 235, 125, 23, 25, 12, 2
	    }
	    ,
	    { // white (3)
		23, 34, 235, 345, 35, 23, 3, 34
	    }
	    ,
	    { // green (4)
		14, 34, 145, 345, 34, 4, 14, 45
	    }
	    ,
	    { // orange (5)
		235, 125, 145, 345, 35, 45, 15, 25
	    }
	}
	;
	tempCenterOwnership = new int [centerOwnership.length] [centerOwnership [0].length];

    } // initializeThreeCube method


    public static void faceDeterminer ()
	// ^ takes the center shapes and determines which shape is the
	//   "Front" face, which is the "Right" face, the "Up" face, etc.
	//   and then records this order into the "orientation" array.
    {
	int i,
	    maxIndexX, minIndexX,
	    maxIndexY, minIndexY,
	    maxIndexZ, minIndexZ;
	double max, min;
	double[] [] centerPoints = new double [6] [4];
	/* ^ centerPoints [face identifier][coordinate identifier]
		[...][1] = x coordinate
		[...][2] = y coordinate
		[...][3] = z coordinate

		[0][...] = red face
		[1][...] = yellow face
		[2][...] = blue face
		[3][...] = white face
		[4][...] = green face
		[5][...] = orange face */


	// Putting the center coordinates into the "centerPoints" array

	for (i = 1 ; i <= 3 ; i++)
	{
	    centerPoints [0] [i] = centerRed [0] [i];
	    centerPoints [1] [i] = centerYellow [0] [i];
	    centerPoints [2] [i] = centerBlue [0] [i];
	    centerPoints [3] [i] = centerWhite [0] [i];
	    centerPoints [4] [i] = centerGreen [0] [i];
	    centerPoints [5] [i] = centerOrange [0] [i];
	}


	// Determining Left and Right

	max = -99999;
	maxIndexX = -1;

	for (i = 0 ; i < 6 ; i++)
	{
	    if (centerPoints [i] [1] > max)
	    {
		max = centerPoints [i] [1];
		maxIndexX = i;
	    }
	}

	minIndexX = oppositeSides [1] [maxIndexX];

	orientation [0] = maxIndexX; // right
	orientation [1] = minIndexX; // left


	// Determining Up and Down

	max = -99999;
	maxIndexY = -1;

	for (i = 0 ; i < 6 ; i++)
	{
	    if (centerPoints [i] [2] > max
		    && i != maxIndexX
		    && i != minIndexX)
	    {
		max = centerPoints [i] [2];
		maxIndexY = i;
	    }
	}

	minIndexY = oppositeSides [1] [maxIndexY];

	orientation [2] = minIndexY; // down
	orientation [3] = maxIndexY; // up


	// Determining Front and Back

	max = -99999;
	maxIndexZ = -1;

	for (i = 0 ; i < 6 ; i++)
	{
	    if (centerPoints [i] [3] > max
		    && i != maxIndexX
		    && i != minIndexX
		    && i != maxIndexY
		    && i != minIndexY)
	    {
		max = centerPoints [i] [3];
		maxIndexZ = i;
	    }
	}

	minIndexZ = oppositeSides [1] [maxIndexZ];

	orientation [4] = maxIndexZ; // back
	orientation [5] = minIndexZ; // front

    } // faceDeterminer method


    public static void identifyInput (int input)
    {
	int clockwise;

	// Same Last Input?

	needConverting = true;

	if (!turning)
	{
	    if (input == ctrlX || input == ctrlX1) // pitch
		thisInput = 'p';

	    else if (input == ctrlY || input == ctrlY1) // yaw
		thisInput = 'y';

	    else if (input == ctrlZ || input == ctrlZ1) // roll
		thisInput = 'r';

	    else if (gameMode > 1)
	    {
		if (input == ctrlR || input == ctrlR1) // right
		    thisInput = 'R';

		else if (input == ctrlL || input == ctrlL1) // left
		    thisInput = 'L';

		else if (input == ctrlD || input == ctrlD1) // down
		    thisInput = 'D';

		else if (input == ctrlU || input == ctrlU1) // up
		    thisInput = 'U';

		else if (input == ctrlB || input == ctrlB1) // back
		    thisInput = 'B';

		else if (input == ctrlF || input == ctrlF1) // front
		    thisInput = 'F';

		else
		{
		    turning = false;
		    thisInput = '-';
		}
	    }

	    else
	    {
		turning = false;
		thisInput = '-';
	    }
	}

	// Orienting control axes

	if (thisInput != lastInput)
	{
	    orientControlAxes
		(returnCenter (0), returnCenter (3), returnCenter (4));

	    centerRed = convertToControlAxes (centerRed);
	    centerYellow = convertToControlAxes (centerYellow);
	    centerBlue = convertToControlAxes (centerBlue);
	    centerWhite = convertToControlAxes (centerWhite);
	    centerGreen = convertToControlAxes (centerGreen);
	    centerOrange = convertToControlAxes (centerOrange);

	    if (gameMode > 1)
	    {
		cornerRedBlueWhite = convertToControlAxes (cornerRedBlueWhite);
		cornerRedYellowBlue = convertToControlAxes (cornerRedYellowBlue);
		cornerRedYellowGreen = convertToControlAxes (cornerRedYellowGreen);
		cornerRedWhiteGreen = convertToControlAxes (cornerRedWhiteGreen);
		cornerBlueWhiteOrange = convertToControlAxes (cornerBlueWhiteOrange);
		cornerYellowBlueOrange = convertToControlAxes (cornerYellowBlueOrange);
		cornerYellowGreenOrange = convertToControlAxes (cornerYellowGreenOrange);
		cornerWhiteGreenOrange = convertToControlAxes (cornerWhiteGreenOrange);

		if (gameMode > 2)
		{
		    edgeRedWhite = convertToControlAxes (edgeRedWhite);
		    edgeRedBlue = convertToControlAxes (edgeRedBlue);
		    edgeRedYellow = convertToControlAxes (edgeRedYellow);
		    edgeRedGreen = convertToControlAxes (edgeRedGreen);
		    edgeBlueWhite = convertToControlAxes (edgeBlueWhite);
		    edgeYellowBlue = convertToControlAxes (edgeYellowBlue);
		    edgeYellowGreen = convertToControlAxes (edgeYellowGreen);
		    edgeWhiteGreen = convertToControlAxes (edgeWhiteGreen);
		    edgeWhiteOrange = convertToControlAxes (edgeWhiteOrange);
		    edgeBlueOrange = convertToControlAxes (edgeBlueOrange);
		    edgeYellowOrange = convertToControlAxes (edgeYellowOrange);
		    edgeGreenOrange = convertToControlAxes (edgeGreenOrange);
		}
	    }
	}


	// Transformations

	if (thisInput == 'p') // pitch
	{
	    right = input == ctrlX;

	    centerRed = pitcher (centerRed, right, et);
	    centerYellow = pitcher (centerYellow, right, et);
	    centerBlue = pitcher (centerBlue, right, et);
	    centerWhite = pitcher (centerWhite, right, et);
	    centerGreen = pitcher (centerGreen, right, et);
	    centerOrange = pitcher (centerOrange, right, et);

	    if (gameMode > 1)
	    {
		cornerRedBlueWhite = pitcher (cornerRedBlueWhite, right, et);
		cornerRedYellowBlue = pitcher (cornerRedYellowBlue, right, et);
		cornerRedYellowGreen = pitcher (cornerRedYellowGreen, right, et);
		cornerRedWhiteGreen = pitcher (cornerRedWhiteGreen, right, et);
		cornerBlueWhiteOrange = pitcher (cornerBlueWhiteOrange, right, et);
		cornerYellowBlueOrange = pitcher (cornerYellowBlueOrange, right, et);
		cornerYellowGreenOrange = pitcher (cornerYellowGreenOrange, right, et);
		cornerWhiteGreenOrange = pitcher (cornerWhiteGreenOrange, right, et);

		if (gameMode > 2)
		{
		    edgeRedWhite = pitcher (edgeRedWhite, right, et);
		    edgeRedBlue = pitcher (edgeRedBlue, right, et);
		    edgeRedYellow = pitcher (edgeRedYellow, right, et);
		    edgeRedGreen = pitcher (edgeRedGreen, right, et);
		    edgeBlueWhite = pitcher (edgeBlueWhite, right, et);
		    edgeYellowBlue = pitcher (edgeYellowBlue, right, et);
		    edgeYellowGreen = pitcher (edgeYellowGreen, right, et);
		    edgeWhiteGreen = pitcher (edgeWhiteGreen, right, et);
		    edgeWhiteOrange = pitcher (edgeWhiteOrange, right, et);
		    edgeBlueOrange = pitcher (edgeBlueOrange, right, et);
		    edgeYellowOrange = pitcher (edgeYellowOrange, right, et);
		    edgeGreenOrange = pitcher (edgeGreenOrange, right, et);
		}
	    }

	    lastInput = 'p';
	}

	else if (thisInput == 'y') // yaw
	{
	    right = input == ctrlY;

	    centerRed = yawer (centerRed, right, et);
	    centerYellow = yawer (centerYellow, right, et);
	    centerBlue = yawer (centerBlue, right, et);
	    centerWhite = yawer (centerWhite, right, et);
	    centerGreen = yawer (centerGreen, right, et);
	    centerOrange = yawer (centerOrange, right, et);

	    if (gameMode > 1)
	    {
		cornerRedBlueWhite = yawer (cornerRedBlueWhite, right, et);
		cornerRedYellowBlue = yawer (cornerRedYellowBlue, right, et);
		cornerRedYellowGreen = yawer (cornerRedYellowGreen, right, et);
		cornerRedWhiteGreen = yawer (cornerRedWhiteGreen, right, et);
		cornerBlueWhiteOrange = yawer (cornerBlueWhiteOrange, right, et);
		cornerYellowBlueOrange = yawer (cornerYellowBlueOrange, right, et);
		cornerYellowGreenOrange = yawer (cornerYellowGreenOrange, right, et);
		cornerWhiteGreenOrange = yawer (cornerWhiteGreenOrange, right, et);

		if (gameMode > 2)
		{
		    edgeRedWhite = yawer (edgeRedWhite, right, et);
		    edgeRedBlue = yawer (edgeRedBlue, right, et);
		    edgeRedYellow = yawer (edgeRedYellow, right, et);
		    edgeRedGreen = yawer (edgeRedGreen, right, et);
		    edgeBlueWhite = yawer (edgeBlueWhite, right, et);
		    edgeYellowBlue = yawer (edgeYellowBlue, right, et);
		    edgeYellowGreen = yawer (edgeYellowGreen, right, et);
		    edgeWhiteGreen = yawer (edgeWhiteGreen, right, et);
		    edgeWhiteOrange = yawer (edgeWhiteOrange, right, et);
		    edgeBlueOrange = yawer (edgeBlueOrange, right, et);
		    edgeYellowOrange = yawer (edgeYellowOrange, right, et);
		    edgeGreenOrange = yawer (edgeGreenOrange, right, et);
		}
	    }

	    lastInput = 'y';
	}

	else if (thisInput == 'r') // roll
	{
	    right = input == ctrlZ;

	    centerRed = roller (centerRed, right, et);
	    centerYellow = roller (centerYellow, right, et);
	    centerBlue = roller (centerBlue, right, et);
	    centerWhite = roller (centerWhite, right, et);
	    centerGreen = roller (centerGreen, right, et);
	    centerOrange = roller (centerOrange, right, et);

	    if (gameMode > 1)
	    {
		cornerRedBlueWhite = roller (cornerRedBlueWhite, right, et);
		cornerRedYellowBlue = roller (cornerRedYellowBlue, right, et);
		cornerRedYellowGreen = roller (cornerRedYellowGreen, right, et);
		cornerRedWhiteGreen = roller (cornerRedWhiteGreen, right, et);
		cornerBlueWhiteOrange = roller (cornerBlueWhiteOrange, right, et);
		cornerYellowBlueOrange = roller (cornerYellowBlueOrange, right, et);
		cornerYellowGreenOrange = roller (cornerYellowGreenOrange, right, et);
		cornerWhiteGreenOrange = roller (cornerWhiteGreenOrange, right, et);

		if (gameMode > 2)
		{
		    edgeRedWhite = roller (edgeRedWhite, right, et);
		    edgeRedBlue = roller (edgeRedBlue, right, et);
		    edgeRedYellow = roller (edgeRedYellow, right, et);
		    edgeRedGreen = roller (edgeRedGreen, right, et);
		    edgeBlueWhite = roller (edgeBlueWhite, right, et);
		    edgeYellowBlue = roller (edgeYellowBlue, right, et);
		    edgeYellowGreen = roller (edgeYellowGreen, right, et);
		    edgeWhiteGreen = roller (edgeWhiteGreen, right, et);
		    edgeWhiteOrange = roller (edgeWhiteOrange, right, et);
		    edgeBlueOrange = roller (edgeBlueOrange, right, et);
		    edgeYellowOrange = roller (edgeYellowOrange, right, et);
		    edgeGreenOrange = roller (edgeGreenOrange, right, et);
		}
	    }

	    lastInput = 'r';
	}

	else if (thisInput == 'R') // Right or Right Inverted
	{
	    right = input == ctrlR;
	    pitchAllShapes (0, right);

	    // Rotating the centerOwnership array

	    if (counter >= fastSpinStop)
	    {
		turning = false;
		et = currentSpin;

		if (right)
		    clockwise = 0;
		else
		    clockwise = 1;

		rotateCenterOwnerships (0, 1, cornersThatNeedRotating, cornerIndexes, clockwise, true);
		if (gameMode > 2)
		    rotateCenterOwnerships (0, 1, edgesThatNeedRotating, edgeIndexes, clockwise, false);
	    }
	}

	else if (thisInput == 'L') // Left or Left Inverted
	{
	    right = input == ctrlL1;
	    pitchAllShapes (1, right);

	    // Rotating the centerOwnership array

	    if (counter >= fastSpinStop)
	    {
		turning = false;
		et = currentSpin;

		if (right)
		    clockwise = 0;
		else
		    clockwise = 1;

		rotateCenterOwnerships (0, 1, cornersThatNeedRotating, cornerIndexes, clockwise, true);
		if (gameMode > 2)
		    rotateCenterOwnerships (0, 1, edgesThatNeedRotating, edgeIndexes, clockwise, false);
	    }
	}

	else if (thisInput == 'D') // Down or Down Inverted
	{
	    right = input == ctrlD1;
	    yawAllShapes (2, right);

	    // Rotating the centerOwnership array

	    if (counter >= fastSpinStop)
	    {
		turning = false;
		et = currentSpin;

		if (right)
		    clockwise = 2;
		else
		    clockwise = 3;

		rotateCenterOwnerships (2, 3, cornersThatNeedRotating, cornerIndexes, clockwise, true);
		if (gameMode > 2)
		    rotateCenterOwnerships (2, 3, edgesThatNeedRotating, edgeIndexes, clockwise, false);
	    }
	}

	else if (thisInput == 'U') // Up or Up Inverted
	{
	    right = input == ctrlU;
	    yawAllShapes (3, right);

	    // Rotating the centerOwnership array

	    if (counter >= fastSpinStop)
	    {
		turning = false;
		et = currentSpin;

		if (right)
		    clockwise = 2;
		else
		    clockwise = 3;

		rotateCenterOwnerships (2, 3, cornersThatNeedRotating, cornerIndexes, clockwise, true);
		if (gameMode > 2)
		    rotateCenterOwnerships (2, 3, edgesThatNeedRotating, edgeIndexes, clockwise, false);
	    }
	}

	else if (thisInput == 'B') // Back or Back Inverted
	{
	    right = input == ctrlB1;
	    rollAllShapes (4, right);

	    // Rotating the centerOwnership array

	    if (counter >= fastSpinStop)
	    {
		turning = false;
		et = currentSpin;

		if (right)
		    clockwise = 4;
		else
		    clockwise = 5;

		rotateCenterOwnerships (4, 5, cornersThatNeedRotating, cornerIndexes, clockwise, true);
		if (gameMode > 2)
		    rotateCenterOwnerships (4, 5, edgesThatNeedRotating, edgeIndexes, clockwise, false);
	    }
	}

	else if (thisInput == 'F') // Front or Front Inverted
	{
	    right = input == ctrlF;
	    rollAllShapes (5, right);

	    // Rotating the centerOwnership array

	    if (counter >= fastSpinStop)
	    {
		turning = false;
		et = currentSpin;

		if (right)
		    clockwise = 4;
		else
		    clockwise = 5;

		rotateCenterOwnerships (4, 5, cornersThatNeedRotating, cornerIndexes, clockwise, true);
		if (gameMode > 2)
		    rotateCenterOwnerships (4, 5, edgesThatNeedRotating, edgeIndexes, clockwise, false);
	    }
	}

	else
	    needConverting = false;


	// Converting from control axes

	if (needConverting)
	{
	    centerRed = convertFromControlAxes (centerRed);
	    centerYellow = convertFromControlAxes (centerYellow);
	    centerBlue = convertFromControlAxes (centerBlue);
	    centerWhite = convertFromControlAxes (centerWhite);
	    centerGreen = convertFromControlAxes (centerGreen);
	    centerOrange = convertFromControlAxes (centerOrange);

	    if (gameMode > 1)
	    {
		cornerRedBlueWhite = convertFromControlAxes (cornerRedBlueWhite);
		cornerRedYellowBlue = convertFromControlAxes (cornerRedYellowBlue);
		cornerRedYellowGreen = convertFromControlAxes (cornerRedYellowGreen);
		cornerRedWhiteGreen = convertFromControlAxes (cornerRedWhiteGreen);
		cornerBlueWhiteOrange = convertFromControlAxes (cornerBlueWhiteOrange);
		cornerYellowBlueOrange = convertFromControlAxes (cornerYellowBlueOrange);
		cornerYellowGreenOrange = convertFromControlAxes (cornerYellowGreenOrange);
		cornerWhiteGreenOrange = convertFromControlAxes (cornerWhiteGreenOrange);

		if (gameMode > 2)
		{
		    edgeRedWhite = convertFromControlAxes (edgeRedWhite);
		    edgeRedBlue = convertFromControlAxes (edgeRedBlue);
		    edgeRedYellow = convertFromControlAxes (edgeRedYellow);
		    edgeRedGreen = convertFromControlAxes (edgeRedGreen);
		    edgeBlueWhite = convertFromControlAxes (edgeBlueWhite);
		    edgeYellowBlue = convertFromControlAxes (edgeYellowBlue);
		    edgeYellowGreen = convertFromControlAxes (edgeYellowGreen);
		    edgeWhiteGreen = convertFromControlAxes (edgeWhiteGreen);
		    edgeWhiteOrange = convertFromControlAxes (edgeWhiteOrange);
		    edgeBlueOrange = convertFromControlAxes (edgeBlueOrange);
		    edgeYellowOrange = convertFromControlAxes (edgeYellowOrange);
		    edgeGreenOrange = convertFromControlAxes (edgeGreenOrange);
		}
	    }
	}
    } // identifyInput method


    public static void pitchAllShapes (int center, boolean right)
    {
	if (gameMode > 1)
	{
	    index = 0;

	    if (counter == 0)
	    {
		turning = true;
		et = fastSpin;
	    }

	    // Corners

	    if (isCenterConnected ((int) cornerRedBlueWhite [0] [7], center, true))
	    {
		cornerRedBlueWhite = pitcher (cornerRedBlueWhite, right, et);
		if (counter >= fastSpinStop)
		{
		    cornersThatNeedRotating [index] = (int) cornerRedBlueWhite [0] [7];
		    index++;
		}
	    }
	    if (isCenterConnected ((int) cornerRedYellowBlue [0] [7], center, true))
	    {
		cornerRedYellowBlue = pitcher (cornerRedYellowBlue, right, et);
		if (counter >= fastSpinStop)
		{
		    cornersThatNeedRotating [index] = (int) cornerRedYellowBlue [0] [7];
		    index++;
		}
	    }
	    if (isCenterConnected ((int) cornerRedYellowGreen [0] [7], center, true))
	    {
		cornerRedYellowGreen = pitcher (cornerRedYellowGreen, right, et);
		if (counter >= fastSpinStop)
		{
		    cornersThatNeedRotating [index] = (int) cornerRedYellowGreen [0] [7];
		    index++;
		}
	    }
	    if (isCenterConnected ((int) cornerRedWhiteGreen [0] [7], center, true))
	    {
		cornerRedWhiteGreen = pitcher (cornerRedWhiteGreen, right, et);
		if (counter >= fastSpinStop)
		{
		    cornersThatNeedRotating [index] = (int) cornerRedWhiteGreen [0] [7];
		    index++;
		}
	    }
	    if (isCenterConnected ((int) cornerBlueWhiteOrange [0] [7], center, true))
	    {
		cornerBlueWhiteOrange = pitcher (cornerBlueWhiteOrange, right, et);
		if (counter >= fastSpinStop)
		{
		    cornersThatNeedRotating [index] = (int) cornerBlueWhiteOrange [0] [7];
		    index++;
		}
	    }
	    if (isCenterConnected ((int) cornerYellowBlueOrange [0] [7], center, true))
	    {
		cornerYellowBlueOrange = pitcher (cornerYellowBlueOrange, right, et);
		if (counter >= fastSpinStop)
		{
		    cornersThatNeedRotating [index] = (int) cornerYellowBlueOrange [0] [7];
		    index++;
		}
	    }
	    if (isCenterConnected ((int) cornerYellowGreenOrange [0] [7], center, true))
	    {
		cornerYellowGreenOrange = pitcher (cornerYellowGreenOrange, right, et);
		if (counter >= fastSpinStop)
		{
		    cornersThatNeedRotating [index] = (int) cornerYellowGreenOrange [0] [7];
		    index++;
		}
	    }
	    if (isCenterConnected ((int) cornerWhiteGreenOrange [0] [7], center, true))
	    {
		cornerWhiteGreenOrange = pitcher (cornerWhiteGreenOrange, right, et);
		if (counter >= fastSpinStop)
		{
		    cornersThatNeedRotating [index] = (int) cornerWhiteGreenOrange [0] [7];
		    index++;
		}
	    }
	    if (gameMode > 2)
	    {
		index = 0;

		// Edges

		if (isCenterConnected ((int) edgeRedWhite [0] [7], center, false))
		{
		    edgeRedWhite = pitcher (edgeRedWhite, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeRedWhite [0] [7];
			index++;
		    }
		}
		if (isCenterConnected ((int) edgeRedBlue [0] [7], center, false))
		{
		    edgeRedBlue = pitcher (edgeRedBlue, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeRedBlue [0] [7];
			index++;
		    }
		}
		if (isCenterConnected ((int) edgeRedYellow [0] [7], center, false))
		{
		    edgeRedYellow = pitcher (edgeRedYellow, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeRedYellow [0] [7];
			index++;
		    }
		}
		if (isCenterConnected ((int) edgeRedGreen [0] [7], center, false))
		{
		    edgeRedGreen = pitcher (edgeRedGreen, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeRedGreen [0] [7];
			index++;
		    }
		}
		if (isCenterConnected ((int) edgeBlueWhite [0] [7], center, false))
		{
		    edgeBlueWhite = pitcher (edgeBlueWhite, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeBlueWhite [0] [7];
			index++;
		    }
		}
		if (isCenterConnected ((int) edgeYellowBlue [0] [7], center, false))
		{
		    edgeYellowBlue = pitcher (edgeYellowBlue, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeYellowBlue [0] [7];
			index++;
		    }
		}
		if (isCenterConnected ((int) edgeYellowGreen [0] [7], center, false))
		{
		    edgeYellowGreen = pitcher (edgeYellowGreen, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeYellowGreen [0] [7];
			index++;
		    }
		}
		if (isCenterConnected ((int) edgeWhiteGreen [0] [7], center, false))
		{
		    edgeWhiteGreen = pitcher (edgeWhiteGreen, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeWhiteGreen [0] [7];
			index++;
		    }
		}
		if (isCenterConnected ((int) edgeWhiteOrange [0] [7], center, false))
		{
		    edgeWhiteOrange = pitcher (edgeWhiteOrange, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeWhiteOrange [0] [7];
			index++;
		    }
		}
		if (isCenterConnected ((int) edgeBlueOrange [0] [7], center, false))
		{
		    edgeBlueOrange = pitcher (edgeBlueOrange, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeBlueOrange [0] [7];
			index++;
		    }
		}
		if (isCenterConnected ((int) edgeYellowOrange [0] [7], center, false))
		{
		    edgeYellowOrange = pitcher (edgeYellowOrange, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeYellowOrange [0] [7];
			index++;
		    }
		}
		if (isCenterConnected ((int) edgeGreenOrange [0] [7], center, false))
		{
		    edgeGreenOrange = pitcher (edgeGreenOrange, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeGreenOrange [0] [7];
			index++;
		    }
		}

		// Centers

		if (returnCenter (center) [0] == centerRed [0] [0])
		    centerRed = pitcher (centerRed, right, et);

		if (returnCenter (center) [0] == centerYellow [0] [0])
		    centerYellow = pitcher (centerYellow, right, et);

		if (returnCenter (center) [0] == centerBlue [0] [0])
		    centerBlue = pitcher (centerBlue, right, et);

		if (returnCenter (center) [0] == centerWhite [0] [0])
		    centerWhite = pitcher (centerWhite, right, et);

		if (returnCenter (center) [0] == centerGreen [0] [0])
		    centerGreen = pitcher (centerGreen, right, et);

		if (returnCenter (center) [0] == centerOrange [0] [0])
		    centerOrange = pitcher (centerOrange, right, et);
	    }
	}
    } // pitchAllShapes method


    public static void yawAllShapes (int center, boolean right)
    {
	if (gameMode > 1)
	{
	    index = 0;

	    if (counter == 0)
	    {
		turning = true;
		et = fastSpin;
	    }

	    // Corners

	    if (isCenterConnected ((int) cornerRedBlueWhite [0] [7], center, true))
	    {
		cornerRedBlueWhite = yawer (cornerRedBlueWhite, right, et);
		if (counter >= fastSpinStop)
		{
		    cornersThatNeedRotating [index] = (int) cornerRedBlueWhite [0] [7];
		    index++;
		}
	    }
	    if (isCenterConnected ((int) cornerRedYellowBlue [0] [7], center, true))
	    {
		cornerRedYellowBlue = yawer (cornerRedYellowBlue, right, et);
		if (counter >= fastSpinStop)
		{
		    cornersThatNeedRotating [index] = (int) cornerRedYellowBlue [0] [7];
		    index++;
		}
	    }
	    if (isCenterConnected ((int) cornerRedYellowGreen [0] [7], center, true))
	    {
		cornerRedYellowGreen = yawer (cornerRedYellowGreen, right, et);
		if (counter >= fastSpinStop)
		{
		    cornersThatNeedRotating [index] = (int) cornerRedYellowGreen [0] [7];
		    index++;
		}
	    }
	    if (isCenterConnected ((int) cornerRedWhiteGreen [0] [7], center, true))
	    {
		cornerRedWhiteGreen = yawer (cornerRedWhiteGreen, right, et);
		if (counter >= fastSpinStop)
		{
		    cornersThatNeedRotating [index] = (int) cornerRedWhiteGreen [0] [7];
		    index++;
		}
	    }
	    if (isCenterConnected ((int) cornerBlueWhiteOrange [0] [7], center, true))
	    {
		cornerBlueWhiteOrange = yawer (cornerBlueWhiteOrange, right, et);
		if (counter >= fastSpinStop)
		{
		    cornersThatNeedRotating [index] = (int) cornerBlueWhiteOrange [0] [7];
		    index++;
		}
	    }
	    if (isCenterConnected ((int) cornerYellowBlueOrange [0] [7], center, true))
	    {
		cornerYellowBlueOrange = yawer (cornerYellowBlueOrange, right, et);
		if (counter >= fastSpinStop)
		{
		    cornersThatNeedRotating [index] = (int) cornerYellowBlueOrange [0] [7];
		    index++;
		}
	    }
	    if (isCenterConnected ((int) cornerYellowGreenOrange [0] [7], center, true))
	    {
		cornerYellowGreenOrange = yawer (cornerYellowGreenOrange, right, et);
		if (counter >= fastSpinStop)
		{
		    cornersThatNeedRotating [index] = (int) cornerYellowGreenOrange [0] [7];
		    index++;
		}
	    }
	    if (isCenterConnected ((int) cornerWhiteGreenOrange [0] [7], center, true))
	    {
		cornerWhiteGreenOrange = yawer (cornerWhiteGreenOrange, right, et);
		if (counter >= fastSpinStop)
		{
		    cornersThatNeedRotating [index] = (int) cornerWhiteGreenOrange [0] [7];
		    index++;
		}
	    }
	    if (gameMode > 2)
	    {
		index = 0;

		// Edges

		if (isCenterConnected ((int) edgeRedWhite [0] [7], center, false))
		{
		    edgeRedWhite = yawer (edgeRedWhite, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeRedWhite [0] [7];
			index++;
		    }
		}
		if (isCenterConnected ((int) edgeRedBlue [0] [7], center, false))
		{
		    edgeRedBlue = yawer (edgeRedBlue, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeRedBlue [0] [7];
			index++;
		    }
		}
		if (isCenterConnected ((int) edgeRedYellow [0] [7], center, false))
		{
		    edgeRedYellow = yawer (edgeRedYellow, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeRedYellow [0] [7];
			index++;
		    }
		}
		if (isCenterConnected ((int) edgeRedGreen [0] [7], center, false))
		{
		    edgeRedGreen = yawer (edgeRedGreen, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeRedGreen [0] [7];
			index++;
		    }
		}
		if (isCenterConnected ((int) edgeBlueWhite [0] [7], center, false))
		{
		    edgeBlueWhite = yawer (edgeBlueWhite, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeBlueWhite [0] [7];
			index++;
		    }
		}
		if (isCenterConnected ((int) edgeYellowBlue [0] [7], center, false))
		{
		    edgeYellowBlue = yawer (edgeYellowBlue, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeYellowBlue [0] [7];
			index++;
		    }
		}
		if (isCenterConnected ((int) edgeYellowGreen [0] [7], center, false))
		{
		    edgeYellowGreen = yawer (edgeYellowGreen, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeYellowGreen [0] [7];
			index++;
		    }
		}
		if (isCenterConnected ((int) edgeWhiteGreen [0] [7], center, false))
		{
		    edgeWhiteGreen = yawer (edgeWhiteGreen, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeWhiteGreen [0] [7];
			index++;
		    }
		}
		if (isCenterConnected ((int) edgeWhiteOrange [0] [7], center, false))
		{
		    edgeWhiteOrange = yawer (edgeWhiteOrange, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeWhiteOrange [0] [7];
			index++;
		    }
		}
		if (isCenterConnected ((int) edgeBlueOrange [0] [7], center, false))
		{
		    edgeBlueOrange = yawer (edgeBlueOrange, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeBlueOrange [0] [7];
			index++;
		    }
		}
		if (isCenterConnected ((int) edgeYellowOrange [0] [7], center, false))
		{
		    edgeYellowOrange = yawer (edgeYellowOrange, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeYellowOrange [0] [7];
			index++;
		    }
		}
		if (isCenterConnected ((int) edgeGreenOrange [0] [7], center, false))
		{
		    edgeGreenOrange = yawer (edgeGreenOrange, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeGreenOrange [0] [7];
			index++;
		    }
		}

		// Centers

		if (returnCenter (center) [0] == centerRed [0] [0])
		    centerRed = yawer (centerRed, right, et);

		if (returnCenter (center) [0] == centerYellow [0] [0])
		    centerYellow = yawer (centerYellow, right, et);

		if (returnCenter (center) [0] == centerBlue [0] [0])
		    centerBlue = yawer (centerBlue, right, et);

		if (returnCenter (center) [0] == centerWhite [0] [0])
		    centerWhite = yawer (centerWhite, right, et);

		if (returnCenter (center) [0] == centerGreen [0] [0])
		    centerGreen = yawer (centerGreen, right, et);

		if (returnCenter (center) [0] == centerOrange [0] [0])
		    centerOrange = yawer (centerOrange, right, et);
	    }
	}
    } // yawAllShapes method


    public static void rollAllShapes (int center, boolean right)
    {
	if (gameMode > 1)
	{
	    index = 0;

	    if (counter == 0)
	    {
		turning = true;
		et = fastSpin;
	    }

	    // Corners

	    if (isCenterConnected ((int) cornerRedBlueWhite [0] [7], center, true))
	    {
		cornerRedBlueWhite = roller (cornerRedBlueWhite, right, et);
		if (counter >= fastSpinStop)
		{
		    cornersThatNeedRotating [index] = (int) cornerRedBlueWhite [0] [7];
		    index++;
		}
	    }
	    if (isCenterConnected ((int) cornerRedYellowBlue [0] [7], center, true))
	    {
		cornerRedYellowBlue = roller (cornerRedYellowBlue, right, et);
		if (counter >= fastSpinStop)
		{
		    cornersThatNeedRotating [index] = (int) cornerRedYellowBlue [0] [7];
		    index++;
		}
	    }
	    if (isCenterConnected ((int) cornerRedYellowGreen [0] [7], center, true))
	    {
		cornerRedYellowGreen = roller (cornerRedYellowGreen, right, et);
		if (counter >= fastSpinStop)
		{
		    cornersThatNeedRotating [index] = (int) cornerRedYellowGreen [0] [7];
		    index++;
		}
	    }
	    if (isCenterConnected ((int) cornerRedWhiteGreen [0] [7], center, true))
	    {
		cornerRedWhiteGreen = roller (cornerRedWhiteGreen, right, et);
		if (counter >= fastSpinStop)
		{
		    cornersThatNeedRotating [index] = (int) cornerRedWhiteGreen [0] [7];
		    index++;
		}
	    }
	    if (isCenterConnected ((int) cornerBlueWhiteOrange [0] [7], center, true))
	    {
		cornerBlueWhiteOrange = roller (cornerBlueWhiteOrange, right, et);
		if (counter >= fastSpinStop)
		{
		    cornersThatNeedRotating [index] = (int) cornerBlueWhiteOrange [0] [7];
		    index++;
		}
	    }
	    if (isCenterConnected ((int) cornerYellowBlueOrange [0] [7], center, true))
	    {
		cornerYellowBlueOrange = roller (cornerYellowBlueOrange, right, et);
		if (counter >= fastSpinStop)
		{
		    cornersThatNeedRotating [index] = (int) cornerYellowBlueOrange [0] [7];
		    index++;
		}
	    }
	    if (isCenterConnected ((int) cornerYellowGreenOrange [0] [7], center, true))
	    {
		cornerYellowGreenOrange = roller (cornerYellowGreenOrange, right, et);
		if (counter >= fastSpinStop)
		{
		    cornersThatNeedRotating [index] = (int) cornerYellowGreenOrange [0] [7];
		    index++;
		}
	    }
	    if (isCenterConnected ((int) cornerWhiteGreenOrange [0] [7], center, true))
	    {
		cornerWhiteGreenOrange = roller (cornerWhiteGreenOrange, right, et);
		if (counter >= fastSpinStop)
		{
		    cornersThatNeedRotating [index] = (int) cornerWhiteGreenOrange [0] [7];
		    index++;
		}
	    }
	    if (gameMode > 2)
	    {
		index = 0;

		// Edges

		if (isCenterConnected ((int) edgeRedWhite [0] [7], center, false))
		{
		    edgeRedWhite = roller (edgeRedWhite, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeRedWhite [0] [7];
			index++;
		    }
		}
		if (isCenterConnected ((int) edgeRedBlue [0] [7], center, false))
		{
		    edgeRedBlue = roller (edgeRedBlue, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeRedBlue [0] [7];
			index++;
		    }
		}
		if (isCenterConnected ((int) edgeRedYellow [0] [7], center, false))
		{
		    edgeRedYellow = roller (edgeRedYellow, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeRedYellow [0] [7];
			index++;
		    }
		}
		if (isCenterConnected ((int) edgeRedGreen [0] [7], center, false))
		{
		    edgeRedGreen = roller (edgeRedGreen, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeRedGreen [0] [7];
			index++;
		    }
		}
		if (isCenterConnected ((int) edgeBlueWhite [0] [7], center, false))
		{
		    edgeBlueWhite = roller (edgeBlueWhite, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeBlueWhite [0] [7];
			index++;
		    }
		}
		if (isCenterConnected ((int) edgeYellowBlue [0] [7], center, false))
		{
		    edgeYellowBlue = roller (edgeYellowBlue, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeYellowBlue [0] [7];
			index++;
		    }
		}
		if (isCenterConnected ((int) edgeYellowGreen [0] [7], center, false))
		{
		    edgeYellowGreen = roller (edgeYellowGreen, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeYellowGreen [0] [7];
			index++;
		    }
		}
		if (isCenterConnected ((int) edgeWhiteGreen [0] [7], center, false))
		{
		    edgeWhiteGreen = roller (edgeWhiteGreen, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeWhiteGreen [0] [7];
			index++;
		    }
		}
		if (isCenterConnected ((int) edgeWhiteOrange [0] [7], center, false))
		{
		    edgeWhiteOrange = roller (edgeWhiteOrange, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeWhiteOrange [0] [7];
			index++;
		    }
		}
		if (isCenterConnected ((int) edgeBlueOrange [0] [7], center, false))
		{
		    edgeBlueOrange = roller (edgeBlueOrange, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeBlueOrange [0] [7];
			index++;
		    }
		}
		if (isCenterConnected ((int) edgeYellowOrange [0] [7], center, false))
		{
		    edgeYellowOrange = roller (edgeYellowOrange, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeYellowOrange [0] [7];
			index++;
		    }
		}
		if (isCenterConnected ((int) edgeGreenOrange [0] [7], center, false))
		{
		    edgeGreenOrange = roller (edgeGreenOrange, right, et);
		    if (counter >= fastSpinStop)
		    {
			edgesThatNeedRotating [index] = (int) edgeGreenOrange [0] [7];
			index++;
		    }
		}

		// Centers

		if (returnCenter (center) [0] == centerRed [0] [0])
		    centerRed = roller (centerRed, right, et);

		if (returnCenter (center) [0] == centerYellow [0] [0])
		    centerYellow = roller (centerYellow, right, et);

		if (returnCenter (center) [0] == centerBlue [0] [0])
		    centerBlue = roller (centerBlue, right, et);

		if (returnCenter (center) [0] == centerWhite [0] [0])
		    centerWhite = roller (centerWhite, right, et);

		if (returnCenter (center) [0] == centerGreen [0] [0])
		    centerGreen = roller (centerGreen, right, et);

		if (returnCenter (center) [0] == centerOrange [0] [0])
		    centerOrange = roller (centerOrange, right, et);
	    }
	}
    } // rollAllShapes mehod


    public static double[] returnCenter (int number)
	// ^ uses the "orientation" array from the "faceDeterminer"
	//  method. This method returns the CONTROL vertex on the face
	//  of a given face orientation (e.g. Front, Right, Up, etc.)
    {
	double[] returnShape = new double [7];

	if (orientation [number] < 3)
	{
	    if (orientation [number] == 0)
		returnShape = centerRed [0];

	    else if (orientation [number] == 1)
		returnShape = centerYellow [0];

	    else // (orientation [number] == 2)
		returnShape = centerBlue [0];
	}
	else
	{
	    if (orientation [number] == 3)
		returnShape = centerWhite [0];

	    else if (orientation [number] == 4)
		returnShape = centerGreen [0];

	    else // (orientation [number] == 5)
		returnShape = centerOrange [0];
	}

	return returnShape;
    }


    public static int returnOrientation (int faceCenter)
	// ^ uses the "orientation" array. Accepts a face center value
	// (0 = red, 1 = yellow, etc.) and returns the corresponding
	// orientation value (0 = right, 1 = left, etc.)
    {
	int i;
	boolean found = false;

	for (i = 0 ; i < orientation.length && !found ; i++)
	    found = faceCenter == orientation [i];

	return i - 1;
    }


    public static boolean isCenterConnected (int cornerIdentifier, int orientation, boolean corner)
	// ^ uses the "centerOwnership" array to determine whether the given
	// corner is connected to the center at the given orientation
    {
	int center = (int) returnCenter (orientation) [0],
	    color1, color2, color3, start, stop;

	if (corner)
	{
	    start = 0;
	    stop = 4;
	}
	else
	{
	    start = 4;
	    stop = 8;
	}

	boolean isConnected = false;

	for (int i = start ; i < stop && !isConnected ; i++)
	    isConnected = cornerIdentifier == centerOwnership [center] [i];

	return isConnected;
    }


    public static void copyArray (int[] [] copyFrom, int[] [] copyTo)
    {
	for (int i = 0 ; i < copyFrom.length ; i++)
	{
	    for (int j = 0 ; j < copyFrom [i].length ; j++)
		copyTo [i] [j] = copyFrom [i] [j];
	}
    }


    public static void rotateCenterOwnerships (int unchanged1, int unchanged2,
	    int[] needRotating, int[] [] indexes, int clockwise, boolean corner)
    {
	int newFace, index, i, j, k, start, stop;

	if (corner)
	{
	    start = 0;
	    stop = 4;
	}
	else
	{
	    start = 4;
	    stop = 8;
	}

	copyArray (centerOwnership, tempCenterOwnership);

	// Finding indexes of corner pieces that can be replaced

	for (i = 0 ; i < centerOwnership.length ; i++) // going through each face center
	{
	    if (i != returnCenter (unchanged1) [0] && i != returnCenter (unchanged2) [0]) // the ownership of the center that was subjected to rotation won't change
	    {
		index = 0;

		for (j = start ; j < stop ; j++)
		{
		    for (k = 0 ; k < needRotating.length ; k++) // going through all of the corner pieces that need rotating
		    {
			if (centerOwnership [i] [j] == needRotating [k])
			{
			    indexes [i] [index] = j;
			    index++;
			}
		    }
		}
	    }
	}


	// Replacing corner pieces as needed

	for (i = 0 ; i < centerOwnership.length ; i++) // going through each face center
	{
	    if (i != returnCenter (unchanged1) [0] && i != returnCenter (unchanged2) [0]) // not center subject to turn
	    {
		newFace = (int) returnCenter (rotateCenterSheet [clockwise] [returnOrientation (i)]) [0];
		index = 0;

		for (j = start ; j < stop ; j++)
		{
		    for (k = 0 ; k < needRotating.length ; k++) // going through each corner needing rotation
		    {
			if (centerOwnership [i] [j] == needRotating [k])
			{
			    tempCenterOwnership [newFace] [indexes [newFace] [index]] = centerOwnership [i] [j];
			    index++;
			}
		    }
		}
	    }
	}

	copyArray (tempCenterOwnership, centerOwnership);

    } // rotateCenterOwnerships method


    public static void orientControlAxes (double[] controlX,
	    double[] controlY, double[] controlZ)
	// ^ creates the new control axes so that
	//   the "Right" center lies on the x-axis,
	//   the "Up" center lies on the y-axis,
	//   and the "Back" center lies on the z-axis.
    {
	int i;

	control [1] = controlX;
	control [2] = controlY;
	control [3] = controlZ;

	// Calculating Constants

	hypotenuseX1 = Math.sqrt (Math.pow (control [1] [1], 2) +
		Math.pow (control [1] [2], 2) + Math.pow (control [1] [3], 2));

	hypotenuseY1 = Math.sqrt (Math.pow (control [2] [1], 2) +
		Math.pow (control [2] [2], 2) + Math.pow (control [2] [3], 2));

	hypotenuseZ1 = Math.sqrt (Math.pow (control [3] [1], 2) +
		Math.pow (control [3] [2], 2) + Math.pow (control [3] [3], 2));

	for (i = 1 ; i < 4 ; i++)
	{
	    pureXYZ [1] [i] = control [1] [i] / hypotenuseX1;
	    pureXYZ [2] [i] = control [2] [i] / hypotenuseY1;
	    pureXYZ [3] [i] = control [3] [i] / hypotenuseZ1;
	}


	// CALCULATION OF PLANES

	// xy plane

	tempCoefficients = planeThroughPoints
	    (control [1] [1], control [1] [2], control [1] [3],
		control [2] [1], control [2] [2], control [2] [3]);

	axy = tempCoefficients [1];
	bxy = tempCoefficients [2];
	cxy = tempCoefficients [3];

	// xz plane

	tempCoefficients = planeThroughPoints
	    (control [1] [1], control [1] [2], control [1] [3],
		control [3] [1], control [3] [2], control [3] [3]);

	axz = tempCoefficients [1];
	bxz = tempCoefficients [2];
	cxz = tempCoefficients [3];

	// yz plane

	tempCoefficients = planeThroughPoints
	    (control [2] [1], control [2] [2], control [2] [3],
		control [3] [1], control [3] [2], control [3] [3]);

	ayz = tempCoefficients [1];
	byz = tempCoefficients [2];
	cyz = tempCoefficients [3];

    } // orientControlAxes method


    public static double[] planeThroughPoints (double x1, double y1,
	    double z1, double x2, double y2, double z2)
	// ^ gives the A, B, and C values of the equation of
	// the plane that contains the origin (0,0,0), and the
	// two given points: (x1, y1, z1) and (x2, y2, z2).

	// The equation of this plane:
	// Ax + By + Cz = 0
    {
	double denominator;
	double[] coefficients = new double [4];
	/* ^ coefficients [1] = A
	     coefficients [2] = B
	     coefficients [3] = C   */

	coefficients [1] = (y1 * z2) - (y2 * z1);
	coefficients [2] = (x2 * z1) - (x1 * z2);
	coefficients [3] = (x1 * y2) - (x2 * y1);

	return coefficients;
    }


    public static double[] [] convertToControlAxes (double[] [] shape)
	// ^ takes the (x0, y0, z0) coordinates of the given vertices
	// and converts them to the (x1, y1, z1) coordinates, which
	// are relative to the control axes.
    {
	double[] tempCoordinates = new double [4];
	double xPos, yPos, zPos;
	int i;

	for (i = 0 ; i < shape.length ; i++)
	{
	    dz = (axy * shape [i] [1]) + (bxy * shape [i] [2]) + (cxy * shape [i] [3]);
	    dy = (axz * shape [i] [1]) + (bxz * shape [i] [2]) + (cxz * shape [i] [3]);
	    dx = (ayz * shape [i] [1]) + (byz * shape [i] [2]) + (cyz * shape [i] [3]);


	    // Finding X Prime

	    tempCoordinates = solvePlane
		(ayz, byz, cyz, dx,
		    axz, bxz, cxz, 0,
		    axy, bxy, cxy, 0);

	    deltaPrimes [1] [1] = tempCoordinates [1];
	    deltaPrimes [1] [2] = tempCoordinates [2];
	    deltaPrimes [1] [3] = tempCoordinates [3];

	    if (deltaPrimes [1] [1] != 0)
	    {
		xPos = Math.abs (deltaPrimes [1] [1]) / deltaPrimes [1] [1]
		    * Math.abs (control [1] [1]) / control [1] [1];
	    }
	    else
		xPos = 1;

	    shape [i] [4] = Math.sqrt (Math.pow (deltaPrimes [1] [1], 2)
		    + Math.pow (deltaPrimes [1] [2], 2)
		    + Math.pow (deltaPrimes [1] [3], 2))
		* xPos;

	    // Finding Y Prime

	    tempCoordinates = solvePlane
		(ayz, byz, cyz, 0,
		    axz, bxz, cxz, dy,
		    axy, bxy, cxy, 0);

	    deltaPrimes [2] [1] = tempCoordinates [1];
	    deltaPrimes [2] [2] = tempCoordinates [2];
	    deltaPrimes [2] [3] = tempCoordinates [3];

	    if (deltaPrimes [2] [2] != 0)
	    {
		yPos = Math.abs (deltaPrimes [2] [2]) / deltaPrimes [2] [2]
		    * Math.abs (control [2] [2]) / control [2] [2];
	    }
	    else
		yPos = 1;

	    shape [i] [5] = Math.sqrt (Math.pow (deltaPrimes [2] [1], 2)
		    + Math.pow (deltaPrimes [2] [2], 2)
		    + Math.pow (deltaPrimes [2] [3], 2))
		* yPos;

	    // Finding Z Prime

	    tempCoordinates = solvePlane
		(ayz, byz, cyz, 0,
		    axz, bxz, cxz, 0,
		    axy, bxy, cxy, dz);

	    deltaPrimes [3] [1] = tempCoordinates [1];
	    deltaPrimes [3] [2] = tempCoordinates [2];
	    deltaPrimes [3] [3] = tempCoordinates [3];

	    if (deltaPrimes [3] [3] != 0)
	    {
		zPos = Math.abs (deltaPrimes [3] [3]) / deltaPrimes [3] [3]
		    * Math.abs (control [3] [3]) / control [3] [3];
	    }
	    else
		zPos = 1;

	    shape [i] [6] = Math.sqrt (Math.pow (deltaPrimes [3] [1], 2)
		    + Math.pow (deltaPrimes [3] [2], 2)
		    + Math.pow (deltaPrimes [3] [3], 2))
		* zPos;
	}

	return shape;

    } // convertToControlAxes method


    public static double[] solvePlane (double a1, double b1, double c1,
	    double d1, double a2, double b2, double c2, double d2,
	    double a3, double b3, double c3, double d3)
	// ^ finds the x, y, and z coordinates of the solution of the
	//  three planes that are inputted, given the coefficients
	//  of the equations of the three planes in the format:

	/*
	    (a1 * x) + (b1 * y) + (c1 * z) = d1
	    (a2 * x) + (b2 * y) + (c2 * z) = d2
	    (a3 * x) + (b3 * y) + (c3 * z) = d3
	*/
    {
	double[] [] tempMatrix = new double [3] [3];
	double[] xyz = new double [4];
	/*  xyz[1] = x coordinate
	    xyz[2] = y coordinate
	    xyz[3] = z coordinate   */

	double denominator, numeratorX, numeratorY, numeratorZ;

	// Denominator

	tempMatrix = new double[] []
	{
	    {
		a1, b1, c1
	    }
	    ,
	    {
		a2, b2, c2
	    }
	    ,
	    {
		a3, b3, c3
	    }
	}


	;
	denominator = det (tempMatrix);

	// numeratorX

	tempMatrix = new double[] []
	{
	    {
		d1, b1, c1
	    }
	    ,
	    {
		d2, b2, c2
	    }
	    ,
	    {
		d3, b3, c3
	    }
	}


	;
	numeratorX = det (tempMatrix);

	// numeratorY

	tempMatrix = new double[] []
	{
	    {
		a1, d1, c1
	    }
	    ,
	    {
		a2, d2, c2
	    }
	    ,
	    {
		a3, d3, c3
	    }
	}


	;
	numeratorY = det (tempMatrix);

	// numeratorZ

	tempMatrix = new double[] []
	{
	    {
		a1, b1, d1
	    }
	    ,
	    {
		a2, b2, d2
	    }
	    ,
	    {
		a3, b3, d3
	    }
	}


	;
	numeratorZ = det (tempMatrix);

	// Solving for the point

	xyz [1] = numeratorX / denominator;
	xyz [2] = numeratorY / denominator;
	xyz [3] = numeratorZ / denominator;

	return xyz;

    } // solvePlane method


    public static double det (double[] [] matrix)
    {
	double determinant;

	/* THE DETERMINANT OF A SQUARE MATRIX
	(http://en.wikipedia.org/wiki/Determinant)

	| a  b  c |
	| d  e  f |
	| g  h  i |

	= aei + bfg + cdh
	   - ceg - bdi - afh;  */

	if (matrix.length != 3 || matrix [0].length != 3)
	    determinant = -99999;
	else
	{
	    determinant =
		matrix [0] [0] * matrix [1] [1] * matrix [2] [2]
		+ matrix [0] [1] * matrix [1] [2] * matrix [2] [0]
		+ matrix [0] [2] * matrix [1] [0] * matrix [2] [1]
		- matrix [0] [2] * matrix [1] [1] * matrix [2] [0]
		- matrix [0] [1] * matrix [1] [0] * matrix [2] [2]
		- matrix [0] [0] * matrix [1] [2] * matrix [2] [1];
	}

	return determinant;

    } // det method


    public static double[] [] pitcher (double[] [] shape,
	    boolean clockwise, double turn)
	// ^ rotates a set of points around the x1-axis.
    {
	int i;
	double deltaY, deltaZ, xRadius, tempPitch;

	for (i = 0 ; i < shape.length ; i++)
	{
	    // Getting Y and Z coordinates

	    deltaY = shape [i] [5];
	    deltaZ = shape [i] [6];


	    // PROCESS

	    xRadius = Math.sqrt
		(Math.pow (deltaY, 2) + Math.pow (deltaZ, 2));

	    tempPitch = pitchFinder (xRadius, deltaY, deltaZ);

	    // Incrementing or Decrementing Angle

	    if (clockwise)
		tempPitch -= turn;
	    else
		tempPitch += turn;

	    // Determining new Y and Z

	    deltaY = Math.sin (tempPitch) * xRadius;
	    deltaZ = Math.cos (tempPitch) * xRadius;

	    // Returning new Y and Z coordinates

	    shape [i] [5] = deltaY;
	    shape [i] [6] = deltaZ;
	}

	return shape;

    } // pitcher method


    public static double[] [] yawer (double[] [] shape,
	    boolean clockwise, double turn)
	// ^ rotates a set of points around the y1-axis.
    {
	int i;
	double deltaX, deltaZ, yRadius, tempYaw;

	for (i = 0 ; i < shape.length ; i++)
	{
	    // Getting X and Z coordinates

	    deltaX = shape [i] [4];
	    deltaZ = shape [i] [6];


	    // PROCESS

	    yRadius = Math.sqrt
		(Math.pow (deltaX, 2) + Math.pow (deltaZ, 2));

	    tempYaw = yawFinder (yRadius, deltaX, deltaZ);

	    // Incrementing or Decrementing Angle

	    if (clockwise)
		tempYaw += turn;
	    else
		tempYaw -= turn;

	    // Determining new X and Z

	    deltaX = Math.sin (tempYaw) * yRadius;
	    deltaZ = Math.cos (tempYaw) * yRadius;

	    // Returning new X and Z coordinates

	    shape [i] [4] = deltaX;
	    shape [i] [6] = deltaZ;
	}

	return shape;

    } // yawer method



    public static double[] [] roller (double[] [] shape,
	    boolean clockwise, double turn)
	// ^ rotates a set of points around the z1-axis.
    {
	int i;
	double deltaX, deltaY, zRadius, tempRoll;

	for (i = 0 ; i < shape.length ; i++)
	{
	    // Getting X and Y coordinates

	    deltaX = shape [i] [4];
	    deltaY = shape [i] [5];


	    // PROCESS

	    zRadius = Math.sqrt
		(Math.pow (deltaX, 2) + Math.pow (deltaY, 2));

	    tempRoll = rollFinder (zRadius, deltaX, deltaY);

	    // Incrementing or Decrementing Angle

	    if (clockwise)
		tempRoll += turn;
	    else
		tempRoll -= turn;

	    // Determining new X and Y

	    deltaX = Math.sin (tempRoll) * zRadius;
	    deltaY = Math.cos (tempRoll) * zRadius;

	    // Returning new X and Y coordinates

	    shape [i] [4] = deltaX;
	    shape [i] [5] = deltaY;
	}

	return shape;

    } // roller method


    public static double pitchFinder (double xRadius, double y, double z)
	// ^ finds and returns the current pitch angle
	//   of a point, relative to the x-axis.

	//  > 0 degrees   =  facing away from user
	//  > 90 degrees  =  up
	//  > 180 degrees =  facing user
	//  > -90 degrees =  down
    {
	double pitch;

	if (xRadius != 0)
	{
	    pitch = Math.asin (y / xRadius);

	    if (z < 0)
		pitch = Math.PI - pitch;
	}
	else
	    pitch = 0;

	return pitch;
    }


    public static double yawFinder (double yRadius, double x, double z)
	// ^ finds and returns the current yaw angle
	// of a point, relative to the y-axis.

	// > 0 degrees   =  facing away from user
	// > 90 degrees  =  right
	// > 180 degrees =  facing user
	// > -90 degrees =  left
    {
	double yaw;

	if (yRadius != 0)
	{
	    yaw = Math.asin (x / yRadius);

	    if (z < 0)
		yaw = Math.PI - yaw;
	}
	else
	    yaw = 0;

	return yaw;
    }


    public static double rollFinder (double zRadius, double x, double y)
	// ^ finds and returns the current roll angle
	// of a point, relative to the z-axis.

	// > 0 degrees = up
	// > 90 degrees = right
	// > 180 degrees = down
	// > -90 degrees = left
    {
	double roll;

	if (zRadius != 0)
	{
	    roll = Math.asin (x / zRadius);

	    if (y < 0)
		roll = Math.PI - roll;
	}
	else
	    roll = 0;

	return roll;
    }


    public static double[] [] convertFromControlAxes (double[] [] shape)
	// ^ takes the (x1, y1, z1) coordinates of the given vertices
	// and converts them to the (x0, y0, z0) coordinates, which
	// are relative to the console.
    {
	double[] tempCoordinates = new double [4];
	int i, j;

	for (i = 0 ; i < shape.length ; i++)
	{
	    for (j = 1 ; j < 4 ; j++)
	    {
		deltaPrimes [1] [j] = pureXYZ [1] [j] * shape [i] [4];
		deltaPrimes [2] [j] = pureXYZ [2] [j] * shape [i] [5];
		deltaPrimes [3] [j] = pureXYZ [3] [j] * shape [i] [6];
	    }

	    dx = ayz * deltaPrimes [1] [1] + byz * deltaPrimes [1] [2] + cyz * deltaPrimes [1] [3];
	    dy = axz * deltaPrimes [2] [1] + bxz * deltaPrimes [2] [2] + cxz * deltaPrimes [2] [3];
	    dz = axy * deltaPrimes [3] [1] + bxy * deltaPrimes [3] [2] + cxy * deltaPrimes [3] [3];

	    tempCoordinates = solvePlane
		(ayz, byz, cyz, dx,
		    axz, bxz, cxz, dy,
		    axy, bxy, cxy, dz);

	    shape [i] [1] = tempCoordinates [1];
	    shape [i] [2] = tempCoordinates [2];
	    shape [i] [3] = tempCoordinates [3];
	}

	return shape;
    }


    public static void metaZeroDraw ()
	// ^ handles all of the drawing commands for a 0x0x0 cube
    {
	g.setColor (background);
	g.fillRect (0, 0, consoleWidth, consoleHeight);
	// ^^ Essentially "g.clear" -ing the BufferedImage offscreen

	if (isInstructing)
	    drawInstructions (0);

	c.drawImage (bi, 0, 0, null);
	// ^ drawing the BufferedImage onto the console
    }


    public static void metaOneDraw ()
	// ^ handles all of the drawing commands for a 1x1x1 cube
    {
	int i;

	g.setColor (background);
	g.fillRect (0, 0, consoleWidth, consoleHeight);
	// ^^ Essentially "g.clear" -ing the BufferedImage offscreen

	orderCenters ();

	for (i = 0 ; i < 6 ; i++)
	    draw (vertexOrder (i), useCenter);

	g.setFont (fontDefault);

	if (nudging)
	{
	    g.setColor (Color.red);
	    g.drawString ("NUDGE", 10, 20);
	    g.setColor (Color.black);
	}


	if (isInstructing)
	{
	    drawInstructions (1);
	    // drawFaceOrientations (); // try uncommenting this if you want
	}

	c.drawImage (bi, 0, 0, null);
	// ^ drawing the BufferedImage onto the console

    } // metaOneDraw method


    public static void metaTwoDraw ()
	// ^ handles all of the drawing commands for a 2x2x2 cube
    {
	int i, j;
	double[] [] shape;

	g.setColor (background);
	g.fillRect (0, 0, consoleWidth, consoleHeight);

	orderCenters ();
	orderCorners ();

	for (i = 6 ; i < 14 ; i++)
	{
	    shape = vertexOrder (i);
	    suborderer (shape);

	    for (j = 0 ; j < suborder.length ; j++)
		draw (shape, vertexSuborder (j));
	}

	g.setFont (fontDefault);

	if (nudging)
	{
	    g.setColor (Color.red);
	    g.drawString ("NUDGE", 10, 20);
	    g.setColor (Color.black);
	}

	if (randomizing)
	    g.drawString ("Press any key to stop scrambling.", 210, 30);


	if (isInstructing)
	{
	    drawInstructions (2);
	    // drawFaceOrientations (); // try uncommenting this if you want
	}

	c.drawImage (bi, 0, 0, null);

    } // metaTwoDraw method


    public static void metaThreeDraw ()
	// ^ handles all of the drawing commands for a 3x3x3 cube
    {
	int i, j;
	double[] [] shape;

	g.setColor (background);
	g.fillRect (0, 0, consoleWidth, consoleHeight);

	orderAll ();

	for (i = 0 ; i < 26 ; i++)
	{
	    shape = vertexOrder (i);
	    if (shape.length != 5)
	    {
		suborderer (shape);

		for (j = 0 ; j < suborder.length ; j++)
		    draw (shape, vertexSuborder (j));
	    }
	    else
		draw (shape, useCenter);
	}

	g.setFont (fontDefault);

	if (nudging)
	{
	    g.setColor (Color.red);
	    g.drawString ("NUDGE", 10, 20);
	    g.setColor (Color.black);
	}

	if (randomizing)
	    g.drawString ("Press any key to stop scrambling.", 210, 30);


	if (isInstructing)
	{
	    drawInstructions (2);
	    // drawFaceOrientations (); // try uncommenting this if you want
	}

	c.drawImage (bi, 0, 0, null);

    } // metaThreeDraw method


    public static void drawFaceOrientations ()
    {
	int tx = 10, ty = 50, i;

	g.setFont (new Font ("Courier", Font.PLAIN, 12));
	g.setColor (Color.black);

	for (i = order.length - 1 ; i > 2 && ty < 50 + 3 * 16 ; i--)
	{
	    if (order [i] < 6)
	    {
		g.drawString (orientationNames [returnOrientation (order [i])]
			+ ": " + faceNames [order [i]], tx, ty);
		ty += 16;
	    }
	}
    }


    public static void orderCenters ()
	// ^ takes all of the center shapes, finds the max z value of each
	//  shape (i.e. the z value of the vertex that's furthest back),
	//  puts the shapes into descending order based on these max z
	//  values, and then records this order into the "order" array

	// This allows shapes to be drawn so that shapes will not be
	// visible if they are being blocked by another shape in front.
    {
	int i, j;
	double[] maxes = new double [6];

	for (i = 0 ; i < maxes.length ; i++)
	    maxes [i] = -9999;

	// Finding the max z value of each shape

	for (i = 0 ; i < centerRed.length ; i++)
	{
	    maxes [0] = Math.max (maxes [0], centerRed [i] [3]);
	    maxes [1] = Math.max (maxes [1], centerYellow [i] [3]);
	    maxes [2] = Math.max (maxes [2], centerBlue [i] [3]);
	    maxes [3] = Math.max (maxes [3], centerWhite [i] [3]);
	    maxes [4] = Math.max (maxes [4], centerGreen [i] [3]);
	    maxes [5] = Math.max (maxes [5], centerOrange [i] [3]);
	}


	// Resetting "order" array

	for (i = 0 ; i < 6 ; i++)
	    order [i] = i;


	// Sorting shapes into descending order

	for (i = 1 ; i < maxes.length ; i++)
	{
	    j = i;

	    while ((j > 0) && (maxes [j] > maxes [j - 1]))
	    {
		swap (maxes, j, j - 1);
		swap (order, j, j - 1);
		j--;
	    }
	}
    } // orderer method


    public static void orderCorners ()
	// ^ similar to the "orderCenter" array, except it
	// orders corner shapes instead of center shapes.
    {
	int i, j;
	double[] maxes = new double [8];

	for (i = 0 ; i < maxes.length ; i++)
	    maxes [i] = -9999;

	// Finding the max z value of each shape

	for (i = 0 ; i < cornerRedBlueWhite.length ; i++)
	{
	    maxes [0] = Math.max (maxes [0], cornerRedBlueWhite [i] [3]);
	    maxes [1] = Math.max (maxes [1], cornerRedYellowBlue [i] [3]);
	    maxes [2] = Math.max (maxes [2], cornerRedYellowGreen [i] [3]);
	    maxes [3] = Math.max (maxes [3], cornerRedWhiteGreen [i] [3]);
	    maxes [4] = Math.max (maxes [4], cornerBlueWhiteOrange [i] [3]);
	    maxes [5] = Math.max (maxes [5], cornerYellowBlueOrange [i] [3]);
	    maxes [6] = Math.max (maxes [6], cornerYellowGreenOrange [i] [3]);
	    maxes [7] = Math.max (maxes [7], cornerWhiteGreenOrange [i] [3]);
	}


	// Resetting "order" array

	for (i = 6 ; i < 14 ; i++)
	    order [i] = i;


	// Sorting shapes into descending order

	for (i = 1 ; i < maxes.length ; i++)
	{
	    j = i;

	    while ((j > 0) && (maxes [j] > maxes [j - 1]))
	    {
		swap (maxes, j, j - 1);
		swap (order, j + 6, j + 6 - 1);
		j--;
	    }
	}
    } // orderer method


    public static void orderAll ()
	// ^ similar to the "orderCenter" array, except it orders
	//  center, corner, and edge shapes instead of just center shapes.
    {
	int i, j;
	double[] maxes = new double [26];

	for (i = 0 ; i < maxes.length ; i++)
	    maxes [i] = -9999;

	// Finding the max z value of each shape

	for (i = 0 ; i < centerRed.length ; i++)
	{
	    maxes [0] = Math.max (maxes [0], centerRed [i] [3]);
	    maxes [1] = Math.max (maxes [1], centerYellow [i] [3]);
	    maxes [2] = Math.max (maxes [2], centerBlue [i] [3]);
	    maxes [3] = Math.max (maxes [3], centerWhite [i] [3]);
	    maxes [4] = Math.max (maxes [4], centerGreen [i] [3]);
	    maxes [5] = Math.max (maxes [5], centerOrange [i] [3]);
	}

	for (i = 0 ; i < cornerRedBlueWhite.length ; i++)
	{
	    maxes [6] = Math.max (maxes [6], cornerRedBlueWhite [i] [3]);
	    maxes [7] = Math.max (maxes [7], cornerRedYellowBlue [i] [3]);
	    maxes [8] = Math.max (maxes [8], cornerRedYellowGreen [i] [3]);
	    maxes [9] = Math.max (maxes [9], cornerRedWhiteGreen [i] [3]);
	    maxes [10] = Math.max (maxes [10], cornerBlueWhiteOrange [i] [3]);
	    maxes [11] = Math.max (maxes [11], cornerYellowBlueOrange [i] [3]);
	    maxes [12] = Math.max (maxes [12], cornerYellowGreenOrange [i] [3]);
	    maxes [13] = Math.max (maxes [13], cornerWhiteGreenOrange [i] [3]);
	}

	for (i = 0 ; i < edgeRedWhite.length ; i++)
	{
	    maxes [14] = Math.max (maxes [14], edgeRedWhite [i] [3]);
	    maxes [15] = Math.max (maxes [15], edgeRedBlue [i] [3]);
	    maxes [16] = Math.max (maxes [16], edgeRedYellow [i] [3]);
	    maxes [17] = Math.max (maxes [17], edgeRedGreen [i] [3]);
	    maxes [18] = Math.max (maxes [18], edgeBlueWhite [i] [3]);
	    maxes [19] = Math.max (maxes [19], edgeYellowBlue [i] [3]);
	    maxes [20] = Math.max (maxes [20], edgeYellowGreen [i] [3]);
	    maxes [21] = Math.max (maxes [21], edgeWhiteGreen [i] [3]);
	    maxes [22] = Math.max (maxes [22], edgeWhiteOrange [i] [3]);
	    maxes [23] = Math.max (maxes [23], edgeBlueOrange [i] [3]);
	    maxes [24] = Math.max (maxes [24], edgeYellowOrange [i] [3]);
	    maxes [25] = Math.max (maxes [25], edgeGreenOrange [i] [3]);
	}


	// Resetting "order" array

	for (i = 0 ; i < 26 ; i++)
	    order [i] = i;


	// Sorting shapes into descending order

	for (i = 1 ; i < maxes.length ; i++)
	{
	    j = i;

	    while ((j > 0) && (maxes [j] > maxes [j - 1]))
	    {
		swap (maxes, j, j - 1);
		swap (order, j, j - 1);
		j--;
	    }
	}

    } // orderAll method


    public static void suborderer (double[] [] shape)
	// ^ orders the individual squares within corner / edge pieces.
    {
	int i, j;
	double[] maxes = new double [3],
	    shape0 = new double [4],
	    shape1 = new double [4],
	    shape2 = new double [4];
	boolean isInFront = false, isCorner = shape.length > 8;

	// Initialization of Variables

	for (i = 0 ; i < maxes.length ; i++)
	    maxes [i] = -9999;

	for (i = 0 ; i < 4 ; i++)
	{
	    shape0 [i] = shape [i] [3];
	    shape1 [i] = shape [i + 4] [3];
	    if (isCorner)
		shape2 [i] = shape [i + 8] [3];
	}


	// Finding the max z value of each shape

	for (i = 0 ; i < shape0.length ; i++)
	{
	    maxes [0] = Math.max (maxes [0], shape0 [i]);
	    maxes [1] = Math.max (maxes [1], shape1 [i]);
	    if (isCorner)
		maxes [2] = Math.max (maxes [2], shape2 [i]);
	}


	// Resetting "suborder" array

	if (isCorner)
	{
	    suborder = new int[]
	    {
		0, 1, 2
	    }
	    ;
	}
	else
	{
	    suborder = new int[]
	    {
		0, 1
	    }
	    ;
	}


	// Sorting shapes into descending order

	for (i = 1 ; i < maxes.length ; i++)
	{
	    j = i;

	    while ((j > 0) && (maxes [j] > maxes [j - 1]))
	    {
		swap (maxes, j, j - 1);
		swap (suborder, j, j - 1);
		j--;
	    }
	}
    } // suborderer method


    public static void swap (double[] array, int a, int b)
	// ^ swaps 2 elements of a double array.
    {
	double temp = array [a];
	array [a] = array [b];
	array [b] = temp;
    }


    public static void swap (int[] array, int a, int b)
	// ^ swaps 2 elements of an int array.
    {
	int temp = array [a];
	array [a] = array [b];
	array [b] = temp;
    }


    public static double[] [] vertexOrder (int number)
	// ^ uses the "order" array from the "orderer" method.
	// This method returns the shape in a given position.
    {
	double[] [] returnShape = new double [4] [7];

	// Efficient Decision Structure

	if (order [number] < 13)
	{
	    if (order [number] < 6)
	    {
		if (order [number] < 3)
		{
		    if (order [number] == 0)
			returnShape = centerRed; // 0
		    else if (order [number] == 1)
			returnShape = centerYellow; // 1
		    else
			returnShape = centerBlue; // 2
		}
		else
		{
		    if (order [number] == 3)
			returnShape = centerWhite; // 3
		    else if (order [number] == 4)
			returnShape = centerGreen; // 4
		    else
			returnShape = centerOrange; // 5
		}
	    }
	    else
	    {
		if (order [number] < 10)
		{
		    if (order [number] == 6)
			returnShape = cornerRedBlueWhite; // 6
		    else if (order [number] == 7)
			returnShape = cornerRedYellowBlue; // 7
		    else if (order [number] == 8)
			returnShape = cornerRedYellowGreen; // 8
		    else
			returnShape = cornerRedWhiteGreen; // 9
		}
		else
		{
		    if (order [number] == 10)
			returnShape = cornerBlueWhiteOrange; // 10
		    else if (order [number] == 11)
			returnShape = cornerYellowBlueOrange; // 11
		    else
			returnShape = cornerYellowGreenOrange; // 12
		}
	    }
	}
	else
	{
	    if (order [number] < 19)
	    {
		if (order [number] < 16)
		{
		    if (order [number] == 13)
			returnShape = cornerWhiteGreenOrange; // 13
		    else if (order [number] == 14)
			returnShape = edgeRedWhite; // 14
		    else
			returnShape = edgeRedBlue; // 15
		}
		else
		{
		    if (order [number] == 16)
			returnShape = edgeRedYellow; // 16
		    else if (order [number] == 17)
			returnShape = edgeRedGreen; // 17
		    else
			returnShape = edgeBlueWhite; // 18
		}
	    }
	    else
	    {
		if (order [number] < 23)
		{
		    if (order [number] == 19)
			returnShape = edgeYellowBlue; // 19
		    else if (order [number] == 20)
			returnShape = edgeYellowGreen; // 20
		    else if (order [number] == 21)
			returnShape = edgeWhiteGreen; // 21
		    else
			returnShape = edgeWhiteOrange; // 22
		}
		else
		{
		    if (order [number] == 23)
			returnShape = edgeBlueOrange; // 23
		    else if (order [number] == 24)
			returnShape = edgeYellowOrange; // 24
		    else
			returnShape = edgeGreenOrange; // 25
		}
	    }
	}

	return returnShape;

    } // vertexOrder method


    public static int[] vertexSuborder (int number)
	// ^ uses the "suborder" array from the "suborderer" method.
	// Returns an int[] "use" array.
    {
	int[] use = new int [4];

	if (suborder [number] == 0)
	    use = useCorner1;

	else if (suborder [number] == 1)
	    use = useCorner2;

	else if (suborder [number] == 2)
	    use = useCorner3;

	return use;
    }


    public static void draw (double[] [] shape, int[] use)
	// ^ draws the shape onto the console, only using the vertices
	//  defined by the "use" array. It then fills the shape using
	// the color encoded at the [0][0] position in the shape array.
    {
	int i;
	Color color = colorBank [(int) shape [use [0]] [0]];

	// Setting x-coordinates and y-coordinates

	for (i = 0 ; i < use.length ; i++)
	{
	    xPoints [i] = dX (shape [use [i]] [1]);
	    yPoints [i] = dY (shape [use [i]] [2]);
	}


	// Drawing the shape

	g.setColor (color);
	g.fillPolygon (xPoints, yPoints, use.length);

	g.setColor (Color.black);
	g.drawPolygon (xPoints, yPoints, use.length);
    }


    public static int dX (double xCoordinate)
	// ^ converts Cartesian x-coordinates into coordinates
	// that can be used to draw things on the HSA console.
    {
	int altered = roundcast (xCoordinate + h);
	return altered;
    }


    public static int dY (double yCoordinate)
	// ^ converts Cartesian y-coordinates into
	// coordinates that can be used to draw
	// things on the HSA console.

    {
	int altered = roundcast (-yCoordinate + k);
	return altered;
    }


    public static int roundcast (double toRound)
	// ^ rounds AND casts to integer.
    {
	int rounded = (int) Math.round (toRound);
	return rounded;
    }


    public static void easterEgg ()
    {
	/* This is an easter egg:

	http://en.wikipedia.org/wiki/Easter_egg_(media)
	http://tvtropes.org/pmwiki/pmwiki.php/Main/EasterEgg
	*/

	// Declaration of Variables

	Font f1 = new Font ("Gungsuh", Font.PLAIN, 115);
	Font f2 = new Font ("Arial", Font.BOLD, 16);
	Font f3 = new Font ("Arial", Font.BOLD, 9);

	Color darkBack;
	Color back = new Color (245, 245, 245);
	Color title = new Color (35, 26, 21);
	Color roman = new Color (173, 196, 175);
	Color author = new Color (246, 214, 173);

	long startTime;
	double percent;
	int x = 0, y = 0, oX, oY,
	    width, height,
	    red, green, blue,
	    darkR = 158,
	    darkG = 164,
	    darkB = 171;

	// Output loop

	while (!c.isCharAvail ())
	{
	    startTime = System.currentTimeMillis ();

	    // Gradient

	    width = 1200;
	    height = 900;
	    oX = -300;
	    oY = -185;

	    g.setColor (back);
	    g.fillRect (0, 0, 640, 500);

	    for (int i = 0 ; i < 50 ; i++)
	    {
		percent = (i + 0.4 * Math.random ()) / 50.0;
		red = (int) (245 * percent + darkR * (1 - percent));
		green = (int) (245 * percent + darkG * (1 - percent));
		blue = (int) (245 * percent + darkB * (1 - percent));
		darkBack = new Color (red, green, blue);
		g.setColor (darkBack);
		g.fillOval (x + oX, y + oY, width, height);
		oX += 500 / 50;
		oY += 375 / 50;
		width -= 950 / 50;
		height -= 700 / 50;
	    }

	    // Text

	    g.setColor (Color.black);
	    g.setFont (f1);
	    g.drawString ("\u65E5", x + 261, y + 240);
	    g.drawString ("\u5E38", x + 263, y + 346);

	    g.setColor (roman);
	    g.setFont (f2);
	    g.drawString ("nichijou", x + 353, y + 251);

	    g.setColor (author);
	    g.setFont (f3);
	    g.drawString ("arawi keiichi", x + 358, y + 263);

	    if (x > 15)
		x -= (int) 6 * Math.random ();
	    else if (x < -15)
		x += (int) 6 * Math.random ();
	    if (y > 12)
		y -= (int) 6 * Math.random ();
	    else if (y < -12)
		y += (int) 6 * Math.random ();

	    x += (int) (4 * Math.random () - 2);
	    y += (int) (4 * Math.random () - 2);

	    c.drawImage (bi, 0, 0, null);

	    while (System.currentTimeMillis () - startTime <= 33)
	    {
		try
		{
		    Thread.sleep (15);
		}
		catch (InterruptedException e)
		{
		}
	    }
	}

	c.getChar ();

    } // easterEggMethod
} // CubeSimulatorNew class


