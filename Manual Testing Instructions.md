# Manuel Testing Instructions

### Test Window Resizing

* Resize window
    * should still center UI
    * Clickable areas should correspond to new positions
    * Resizing windows should not make any functionality disappear

### Control Panel

* Cards (the blue squares) and Power down (red circle) should darken when hovering over
* Power down button should toggle (darken/lighten) when player is powered down (power down by clicking it)
* Robot should move as specified by handcards
* Cards should darken when at loss of  (locked down)
* It should not be possible to change movement cards that are locked down
* Dragging cards should be precise, somewhat snappy and work as intended
* The correct tooltips should show for each card
* Pressing enter should make the round start


### Camera Movement

* Map should zoom when scrolling
* Map should zoom in when pressing +
* Map should zoom out when pressing -
* Map should move around when pressing and dragging   
* At least half the screen should should be of the map at all times
* Resizing should not affect how many tiles shown vertically and horizontally when on a zoom level.
    * Best way to test is zooming all the way in, then count the number of tiles vertically and horizontally.
    * *Note: It will change a tiny bit if the aspect ratio is way different, as the average between the height and width is used*

### Visualizing current state

* Robot should always face its direction and movement should be in that direction (remember backing up is legal)
* Main player robot should be distinguishable by color