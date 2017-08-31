These are a list of objectives, tests to resolution, and date resolved.

1) Be able to activate healing on pre-determined target.
	+ Store pre-programmed moves and how to activate.
	+ Be able to activate said moves.
	+ Activate moves in non-instant loop.
	+ End program loop upon keypress('Esc').
	
	Encoded but untested on 08/06/2017

2) Determine hp/% of the target, and heal only when target is a certain % down.	(Functioning Macro)
	+ Grab image of target's hp:
		+ Get image from hardcoded rectangle.
		+ Get color from collected avg of pixel color in rectangle.
		+ Identify color (red, green, purple, etc.), and avg variance from identified color.
		+ Simplify rectangle to a continguous int or int[] (array of x points on line y,
			or array of x points across varying y).
		+ Scan rectangle for contiguous int([]) of valid pixels.
	+ Check difference in target hp to full hp.
	+ Check for no health, to terminate program {targetIsDead=true;}.
	+ Tie activated moves into hp% system to avoid healing dead or healthy targets.

	Notes for later: check around health bar for similar-colored pixels,
		to rule out environmental errors.

	Encoded but untested on 08/18/2017
	Tested and finished on 08/31/2017 (Midnight between 30th and 31st to be exact.)

3) Switch between self and target(s).	(Self-Healing Macro)
	+ Storing of hotkeys for selecting self and party target(s).
	+ Grab and check hp of both self and any available party targets to initial images thereof.
	+ Check self health for no hp {my.self.isDead=true;}.
	+ Add cooldown counting so it doesn't spam the skill too much.
	+ Add target names for better code/output notes.

4) Creating, saving, and loading game profiles.		(Starts remembering, paving towards optimization.)
	+ File creation, loading, and updating, with error checking.
	+ Creation of game profile file if not previously created, update thereafter.
	  |~ Similar needs of 1st/Over-shoulder MMORPG games: Character Name, HPBar(s), MPBar(s),
	    Skillbar(s), any party members, any guild/alliance/friend members if needed.
	+ Standardized and/or evolving (later) profiles, to work with multiple games.


Assumptions at this level (Stage 2):
	~ Target needs to be part of their party.
	~ Target needs to remain within range of healing skill.
	- Skillset is hard-coded to AI.
	+ Thanks to command-line args, both target and skills are given upon program startup.
	+ Healing rate is based off target being injured.
	~ All skill usage is done via typed instead of clicked actions.
	+ Target's health bar will not change position, nor color within normal deviation.
	+ If no target in the first party slot, the first target is assumed dead/MIA.