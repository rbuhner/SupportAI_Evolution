These are a list of objectives, tests to resolution, and date resolved.

1) Be able to activate healing on pre-determined target.
	+ Store pre-programmed moves and how to activate.
	+ Be able to activate said moves.
	+ Activate moves in non-instant loop.
	+ End program loop upon keypress('q').

2) Determine hp/% of the target, and heal only when target is a certain % down.	(Functioning Macro)
	+ Grab image of target's hp:
		+ Get image from hardcoded rectangle.
		+ Get color from collected avg of pixel color in rectangle.
		+ Identify color (red, green, purple, etc.), and avg variance from identified color.
		+ 'Blank' out non-color pixels via rectangle array. If two rectangles touch, join them.
		+ Simplify masked rectangle to a continguous int[] or int[][] (array of x points on line y,
			or array of x points across varying y).
		+ Scan rectangle for contiguous int[]/[] of valid pixels.
	+ Check difference in target hp to full hp.
	+ Check for no health, to terminate program {targetIsDead=true;}.
	+ Tie activated moves into hp% system to avoid healing dead or healthy targets.

3) Switch between self and target(s).	(Self-Healing Macro)
	+ Storing of hotkeys for selecting self and party target(s).
	+ Grab and check hp of both self and any available party targets to initial images thereof.
	+ Check self health for no hp {my.self.isDead=true;}.
	+ Add cooldown counting so it doesn't spam the skill too much.
	+ Add target names for better code/output notes.