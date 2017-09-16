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

	Encoded on 09/02/2017
	Tested and finished on 09/04/2017

4) Creating, saving, and loading game profiles.		(Starts remembering, paving towards optimization.)
	+ File creation, loading, and updating, with error checking.
	+ Creation of game profile file if not previously created, update thereafter.
	  |~ Similar needs of 1st/Over-shoulder MMORPG games: Character Name, HPBar(s), MPBar(s),
	    Skillbar(s), any party members, any guild/alliance/friend members if needed.
	+ Standardized and/or evolving (later) profiles, to work with multiple games.

	Encoded on 09/04/2017
	Tested and finished on 09/16/2017

5) Basic OCR (Optical Character Recognition)		(Can work with more diverse inputs.)
	+ Loading and storing specific font(s) and recognition parameters.
	+ Ability to optically read the correct characters in a given area.
	+ Ability to correctly group characters to find targeted words.
	+ Loading and storing of targeted words and "meanings" (currently related actions?).

6) Tracking and Following the currentTarget (given target is not self).		(Starts moving around.)
	+ Visually track the currentTarget on screen
	+ If one loses visual on target, or is about to, move to keep target in sight.
		+ Visually track path the target moves in when possible, and move where they do.

v-------v-------v Not fully developed ideas yet. v-------v-------v

 - Environmental understanding / Visual pathfinding.
 |- Map/Minimap usage, abstract pathfinding.
 |- Wandering/Exploring (Wonder/Curiosity?)

 - Event- and logical-based memory (Theory: The mind is a network/pattern of memories that, when
	certain inputs come up, fit within the input pattern of one or more memories, leading to
	learned reactions (experience -> right decisions, the "I've seen this before" process.)

 - Individuality may be inevitable when two entities are experiencing different space/times, and
	therefore have different memories, but what happens when two entities can 'sync' their memories
	completely? Need a way to maintain individual thought process / individuality even when the
	memories are completely the same.

 - Sound recognition (useful for noticing input outside of visual range, like footsteps or 
	language-independant cries for help.)
 |- Voice/Speech recognition, for further understanding of language, and easier communication.
 |- Music recognition, because it seems a shame to let an intelligence be tone-deaf.

 - Natural Language (of some sort)

 - UI Experimentation (Understanding skill costs and effects through usage, and coming to it's own
	efficiency ex: keeping people alive while not running out of mana [since running out of mana
	tends to kill people too.])

 - 

|------------------------------------------------||------------------------------------------------|

Assumptions at this level (Stage 5):
	~ Target needs to be part of their party.
	~ Target needs to remain within range of healing skill.
	~ Command-line parameters give directory and target file.
	~ Using file profiles, self, targets, and skills are given upon program startup.
	~ Healing rate is based off self and party being injured, restrained by cooldowns.
	~ All skill usage is done via typed instead of clicked actions.
	~ Target's health bar will not change position, nor color within normal deviations.
	~ If no target in the first party slot, the first target is assumed dead/MIA.
	~ Keyboard input to select party members is single-key.
	+ Other commands may be given via on-screen typed 'interface'?