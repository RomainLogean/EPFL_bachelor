;    set game state memory location
.equ    HEAD_X,         0x1000  ; Snake head's position on x
.equ    HEAD_Y,         0x1004  ; Snake head's position on y
.equ    TAIL_X,         0x1008  ; Snake tail's position on x
.equ    TAIL_Y,         0x100C  ; Snake tail's position on Y
.equ    SCORE,          0x1010  ; Score address
.equ    GSA,            0x1014  ; Game state array address

.equ    CP_VALID,       0x1200  ; Whether the checkpoint is valid.
.equ    CP_HEAD_X,      0x1204  ; Snake head's X coordinate. (Checkpoint)
.equ    CP_HEAD_Y,      0x1208  ; Snake head's Y coordinate. (Checkpoint)
.equ    CP_TAIL_X,      0x120C  ; Snake tail's X coordinate. (Checkpoint)
.equ    CP_TAIL_Y,      0x1210  ; Snake tail's Y coordinate. (Checkpoint)
.equ    CP_SCORE,       0x1214  ; Score. (Checkpoint)
.equ    CP_GSA,         0x1218  ; GSA. (Checkpoint)

.equ    LEDS,           0x2000  ; LED address
.equ    SEVEN_SEGS,     0x1198  ; 7-segment display addresses
.equ    RANDOM_NUM,     0x2010  ; Random number generator address
.equ    BUTTONS,        0x2030  ; Buttons addresses

; button state
.equ    BUTTON_NONE,    0
.equ    BUTTON_LEFT,    1
.equ    BUTTON_UP,      2
.equ    BUTTON_DOWN,    3
.equ    BUTTON_RIGHT,   4
.equ    BUTTON_CHECKPOINT,    5

; array state
.equ    DIR_LEFT,       1       ; leftward direction
.equ    DIR_UP,         2       ; upward direction
.equ    DIR_DOWN,       3       ; downward direction
.equ    DIR_RIGHT,      4       ; rightward direction
.equ    FOOD,           5       ; food

; constants
.equ    NB_ROWS,        8       ; number of rows
.equ    NB_COLS,        12      ; number of columns
.equ    NB_CELLS,       96      ; number of cells in GSA
.equ    RET_ATE_FOOD,   1       ; return value for hit_test when food was eaten
.equ    RET_COLLISION,  2       ; return value for hit_test when a collision was detected
.equ    ARG_HUNGRY,     0       ; a0 argument for move_snake when food wasn't eaten
.equ    ARG_FED,        1       ; a0 argument for move_snake when food was eaten

.equ	LEDS1,			0x2004	; LED1 address
.equ	LEDS2,			0x2008	; LED1 address


; initialize stack pointer
addi sp, zero, LEDS


; main
; arguments
;     none
;
; return values
;     This procedure should never return.
main:
    ; TODO: Finish this procedure.

	stw zero, CP_VALID(zero) ; au depart pas de checkpoint valid


	call init_game	;initialise le jeu
	call wait 
	call get_input	;test si on veux changer de dire ou non

	addi s0, zero, 0x5
	bne s0, v0, 28
	call restore_checkpoint; si on appuie sur checkpoint (bouton 5)
	addi s1, zero, 0x1
	bne v0, s1, 12
	call clear_leds
	call draw_array
	call blink_score
	br -44 ; go to clear leds

	call hit_test	;regarde les collitions

	;case end of the game :
	addi s0, zero, 0x2
	bne s0, v0, 0x8 ; si v0 = 2 stop the game
	call wait
	br -68 ;go back to init_game

	;case eating :
	addi s0, zero, 0x1 ; s0 = 1
	bne v0, s0, 0x30 ; v0=1 = collition avec food sinon ->skip
	ldw s1, SCORE(zero); s1 = score
	addi s1, s1, 0x1	; s1 = score+1
	stw s1, SCORE(zero) ; store le nouveau score
	call display_score
	addi a0, zero, 0x1
	call move_snake
	call create_food
	call save_checkpoint
	addi s0, zero, 0x1
	bne v0, s0, 0x4
	call blink_score
	br 0x8

	;si v0 != 2 and v0 != 1 => v0 = 0

	add a0, zero, zero
	call move_snake

	call clear_leds
	call draw_array;

	;addi s6, zero, 0xff ; pour loop dans le vide comme Ã§a tu spamme ctrl + r et ca fait un tour de boucle complet
	;ldw s7, RANDOM_NUM(zero) ; tu met random = ff et Ã§a commence le nouveau tour
	;bne s7, s6, 0xFFFFFFF4

	br -140

	ret


; BEGIN: clear_leds
clear_leds:
	stw zero, LEDS(zero)
    stw zero, LEDS1(zero)
    stw zero, LEDS2(zero)
	ret
; END: clear_leds


; BEGIN: set_pixel
set_pixel:  ; x = a0, y= a1
            ; y + 8 * (x%4)
	andi t0, a0, 0x3 ; x%4
	slli t0, t0, 0x3 ; x * 8
	add t0, t0, a1  ; y + 8(x%4)

    addi t2, zero, 0x1
    sll t2, t2, t0 ; creation du masque

    srli t1, a0, 0x2 ; x/4
    slli t1, t1, 0x2 ; *4 for address
	ldw t3, LEDS(t1)
	or t2, t2, t3 ; pour ne pas eteindre les autres leds
    stw t2, LEDS(t1)
    ret
    

; END: set_pixel


; BEGIN: display_score
display_score:

	;t0 : the Score (change au cour du prog)
	;t1 = 10 puis 1
	;t2 : dizaine
	;t3 : to select correct SEVEN_SEGS

	ldw t0, SCORE(zero) ;pk Ã§a marchait sans cette ligne ?

	addi t1, zero, 0xa ;Il faut gere les cas > 100 (apparemment Ã§a n'est pas testÃ© ... sinon on peut le faire)
	addi t2, zero, 0x0
	addi t3, zero, 0x8


	blt t0, t1, 0xc ; on calcule les dizaine en faisant -10 dans la boucle jusqu'a < 10
	sub t0, t0, t1
	addi t2, t2, 0x1
	br 0xFFFFFFF0

	;add s0, t2, zero ;pour les tests /!\

	slli t2, t2, 0x2 ;on store la valeur de digit_map correspondante dans SEVEN_SEGS
	ldw t2, digit_map(t2) 
	stw t2, SEVEN_SEGS(t3)


	addi t1, zero, 0x1
	addi t2, zero, 0x0
	addi t3, zero, 0xc

	beq t0, zero, 0xc ; on calcule les unitÃ©s en faisant -1 dans la boucle jusqu'a 0
	sub t0, t0, t1
	addi t2, t2, 0x1
	br 0xFFFFFFF0

	;add s1, t2, zero ;pour les tests /!\

	slli t2, t2, 0x2 ;on store la valeur de digit_map correspondante dans SEVEN_SEGS
	ldw t2, digit_map(t2) 
	stw t2, SEVEN_SEGS(t3)

	addi t3, zero, 0x4
	ldw t2, digit_map(zero) 
	stw t2, SEVEN_SEGS(t3)

	addi t3, zero, 0x0
	ldw t2, digit_map(zero) 
	stw t2, SEVEN_SEGS(t3)

	ret



; END: display_score


; BEGIN: init_game
init_game:

	;clear old GSA
	addi t0, zero, 0x60
	add t1, zero, zero

	slli t2, t1, 0x2
	stw zero, GSA(t2)
	addi t1, t1, 0x1
	blt t1, t0, 0xFFFFFFF0


	stw zero, HEAD_X(zero)
	stw zero, HEAD_Y(zero)
	stw zero, TAIL_X(zero)
	stw zero, TAIL_Y(zero)

	;addi s0, ra, 0x0

	addi t1, zero, 0x4
	stw t1, GSA(zero)

	stw zero, SCORE(zero)


	;faut save le pointer
	addi t4, zero, 0x4
	sub sp, sp, t4
	stw ra, 0x0(sp)

	call create_food
	call clear_leds
	call draw_array
	call display_score

	ldw ra, 0x0(sp)
	addi sp, sp, 0x4
ret


; END: init_game


; BEGIN: create_food
create_food:
	;t0 random num (last byte)
	;t2 contenu GSA
	;t3 t0 en forme d'adresse (1 dans GSA == 4 en memoire)
	;t4 max value of random possible
	;addi t4, zero, 0x5F
	
	;ldw t0, RANDOM_NUM(zero)  ; est ce qu'on doit test 
	;andi t0, t0, 0xFF ; get last byte
	;slli t3, t0, 0x2 ; *4 word GSA address of index t0
	;ldw t2, GSA(t3)
	;bltu t4, t0, 0xFFFFFFEC ; si en dehors des limites
	;bne t2, zero, 0xFFFFFFE8 ; si GSA contient qqch (!= 0) alors prendre nouveau random

	; correction ?
	addi t4, zero, NB_CELLS
	
	ldw t0, RANDOM_NUM(zero)  ; est ce qu'on doit test 
	andi t0, t0, 0xFF ; get last byte
	bge t0, t4, create_food ; si en dehors des limites
	slli t3, t0, 0x2 ; *4 word GSA address of index t0
	ldw t2, GSA(t3)
	bne t2, zero, create_food ; si GSA contient qqch (!= 0) alors prendre nouveau random
	
	addi t1, zero, 0x5 ; food
	stw t1, GSA(t3) ; add food at GSA adress
	
	ret

; END: create_food


; BEGIN: hit_test
hit_test:
	;v0 = next direction set by main 
	;t0 = direction (= v0) 
	;t1 == HEAD_X and TAIL_X 
	;t2 == HEAD_Y and TAIL_Y
	;t3 == comparsisons val
	;t4 == 1
	;t5 == next GSA index of head
	;t6 == val of next GSA index
	;v0 return : collition type 0, 1 or 2

	addi t4, zero, 1
	; add t0, zero, v0

	ldw  t1, HEAD_X(zero)
	ldw  t2, HEAD_Y(zero)

	; add t3, zero, zero ; case 0 (keep current direction)
	; bne t0, t3, 0x10
	; t5 == GSA index
	slli t5, t1, 0x3
	add t5, t5, t2
	slli t5, t5, 0x2
	ldw t0, GSA(t5) ; so we search what was the last dir to get the next case
	
	addi t3, zero, 0x1 	; case left (t3 == 1)
	bne t0, t3, 0xc
	sub t1, t1, t4
	addi t3, zero, 0x0 	; test if out of border or not
	blt t1, t3, 0x6C	; next x_head < 0 
	
	addi t3, zero, 0x4	; case right (t3 == 4)
	bne t0, t3, 0xc
	add t1, t1, t4 
	addi t3, zero, 0xb ; test if out of border or not
	blt t3, t1, 0x58 	; next x_head > 11 

	addi t3, zero, 0x3	; case down (t3 == 3)
	bne t0, t3, 0xc
	add t2, t2, t4 
	addi t3, zero, 0x7 	; test if out of border or not
	blt t3, t2, 0x44	; next y_head > 7

	addi t3, zero, 0x2 	; case up (t3 == 2)
	bne t0, t3, 0xc
	sub t2, t2, t4 
	addi t3, zero, 0x0 	; test if out of border or not
	blt t2, t3, 0x30	; next y_head < 0

	slli t5, t1, 0x3 ; we search what is in next head position
	add t5, t5, t2
	slli t5, t5, 0x2
	ldw t6, GSA(t5) ; t6 contain next case obstacle, food or space

	;case food
	addi t3, zero, 0x5
	bne t6, t3, 0x8	; GSA val = 5
	addi v0, zero, 0x1
	ret

	;case nothing
	addi t3, zero, 0x0
	bne t6, t3, 0x8	; GSA val = 0
	addi v0, zero, 0x0
	ret

	; case end of game
	addi v0, zero, 0x2 	; GSA val = 1,2,3,4 (or others but it sould not be the case)
	ret 				; or the next case is out of border


; END: hit_test


; BEGIN: get_input
get_input:

	addi t3, zero, 0x4
	ldw t0, BUTTONS(t3) ; addr edgecapture = BUTTONS + 4 

	bne t0, zero, 0x8
	add v0, zero, zero
	ret

	;get last direction
	ldw  t1, HEAD_X(zero)
	ldw  t2, HEAD_Y(zero)
	slli t5, t1, 0x3
	add t5, t5, t2
	slli t5, t5, 0x2
	ldw t7, GSA(t5) ;;t7 == last direction

	
	addi t1, zero, 0x10
	blt t0, t1, 0xc  ; if !checkpoint -> goto PC + 16
	addi v0, zero, 0x5
	stw zero, BUTTONS(t3) ; clear edgecapture
	ret

	;case RIGHT
	addi t1, zero, 0x1
	beq t7, t1, 0x14 ;check current direction != left (1)
	addi t1, zero, 0x8
	blt t0, t1, 0xc  ;  if !right -> goto PC + 16
	addi v0, zero, 0x4
	stw zero, BUTTONS(t3) ; clear edgecapture
	br 0x50

	;case DOWN
	addi t1, zero, 0x2
	beq t7, t1, 0x14 ;check current direction != up (2)
	addi t1, zero, 0x4
	blt t0, t1, 0xc  ; if !down -> goto PC + 16
	addi v0, zero, 0x3
	stw zero, BUTTONS(t3) ; clear edgecapture
	br 0x34

	;case UP
	addi t1, zero, 0x3
	beq t7, t1, 0x14 ;check current direction != down (3)
	addi t1, zero, 0x2
	blt t0, t1, 0xc  ; if !up -> goto PC + 16
	addi v0, zero, 0x2
	stw zero, BUTTONS(t3) ; clear edgecapture
	br 0x18 ;

	;case LEFT
	addi t1, zero, 0x4
	beq t7, t1, 0x14 ;check current direction != right (4)
	addi t1, zero, 0x1
	blt t0, t1, 0x8  ; if !left -> goto PC + 16
	addi v0, zero, 0x1
	stw zero, BUTTONS(t3) ; clear edgecapture
	
	;ldw  t1, HEAD_X(zero)
	;ldw  t2, HEAD_Y(zero)
	;slli t5, t1, 0x3
	;add t5, t5, t2
	;slli t5, t5, 0x2
	stw v0, GSA(t5) ; so we search what was the last dir to get the next case
	ret

; END: get_input


; BEGIN: draw_array
draw_array:
	; TODO ajouter les waits pour le gecko


	;faut save les register s0-s7 utilisÃ©s par securitÃ©
	addi t4, zero, 0x4
	sub sp, sp, t4
	stw s0, 0x0(sp)
	sub sp, sp, t4
	stw s1, 0x0(sp)
	sub sp, sp, t4
	stw s2, 0x0(sp)
	sub sp, sp, t4
	stw s4, 0x0(sp)
	sub sp, sp, t4
	stw s5, 0x0(sp)

	;s au lieu de t car set_pixel use les t

	addi s4, zero, 0x4 ; s4 = 4
	addi s5, zero, 0x60 ; s5 = 96
	

	;addi s5, zero, 0x8 test premiere collone


	;push ra to stack
	sub sp, sp, s4
	stw ra, 0x0(sp)

	addi s0, zero, 0x0 ; init counter at 0

	;loop
	;s0 counter - 0 to 96
	;s1 direction/food
	;s2 gsa index

	slli s2, s0, 0x2
	ldw s1, GSA(s2)
	beq s1, zero, 0xc ; if zero skip set_pixel
	srli a0, s0, 0x3
	andi a1, s0, 0x7
	call set_pixel
	addi s0, s0, 0x1
	blt s0, s5, 0xFFFFFFE0 ; continue loop as long as GSA > counter

	ldw ra, 0x0(sp)
	add sp, sp, s4

	; on ldw les s0- s5 changÃ©s durant le programme
	ldw s5, 0x0(sp)
	addi sp, sp, 0x4
	ldw s4, 0x0(sp)
	addi sp, sp, 0x4
	ldw s2, 0x0(sp)
	addi sp, sp, 0x4
	ldw s1, 0x0(sp)
	addi sp, sp, 0x4
	ldw s0, 0x0(sp)
	addi sp, sp, 0x4


	ret
	

; END: draw_array


; BEGIN: move_snake			/!\ ne gÃ¨re que les collision avec food !
move_snake:
	;t0 == direction
	;t1 == HEAD_X and TAIL_X 
	;t2 == HEAD_Y and TAIL_Y
	;t3 == comparsisons val
	;t4 == 1
	;t5 == GSA index of head
	;t6 == values for tests
	;;;;;v0 return get_input
	;a0 argument : collision with food
	
	addi t4, zero, 1
	;add t0, zero, v0

	ldw  t1, HEAD_X(zero)
	ldw  t2, HEAD_Y(zero)

	;add t3, zero, zero ; case 0 (keep current direction)
	;bne t0, t3, 0x10
	; t5 == GSA index
	slli t5, t1, 0x3
	add t5, t5, t2
	slli t5, t5, 0x2
	ldw t0, GSA(t5)
	
	addi t3, zero, 0x1 ; case left (t3 == 1)
	bne t0, t3, 0x8
	sub t1, t1, t4
	stw t1, HEAD_X(zero)
	
	addi t3, zero, 0x4	; case right (t3 == 4)
	bne t0, t3, 0x8
	add t1, t1, t4 
	stw t1, HEAD_X(zero)

	addi t3, zero, 0x3	; case down (t3 == 3)
	bne t0, t3, 0x8
	add t2, t2, t4 
	stw t2, HEAD_Y(zero) 

	addi t3, zero, 0x2 	; case up (t3 == 2)
	bne t0, t3, 0x8
	sub t2, t2, t4 
	stw t2, HEAD_Y(zero) 

	slli t5, t1, 0x3 ; t5 == GSA index
	add t5, t5, t2
	slli t5, t5, 0x2
	stw t0, GSA(t5)


	beq a0, zero, 0x4 ; if a0 = 0 (pas de colisions) else if a=1 collision (with food) -> tail remain same
	ret

	ldw t1, TAIL_X(zero)
	ldw t2, TAIL_Y(zero)

	slli t5, t1, 0x3 ; t5 == GSA index of the tail
	add t5, t5, t2
	slli t5, t5, 0x2
	ldw t0, GSA(t5) ; t0 == dir of the tail

	stw zero, GSA(t5)   ; met un 0 a l'endoit de l'ancienne queue 

	addi t3, zero, 0x1 ; case left (t3 == 1)
	bne t0, t3, 0x8
	sub t1, t1, t4
	stw t1, TAIL_X(zero)
	
	addi t3, zero, 0x4	; case right (t3 == 4)
	bne t0, t3, 0x8
	add t1, t1, t4 
	stw t1, TAIL_X(zero)

	addi t3, zero, 0x3	; case down (t3 == 3)
	bne t0, t3, 0x8
	add t2, t2, t4 
	stw t2, TAIL_Y(zero) 

	addi t3, zero, 0x2 	; case up (t3 == 2)
	bne t0, t3, 0x8
	sub t2, t2, t4 
	stw t2, TAIL_Y(zero) 

	ret

	
; END: move_snake


; BEGIN: save_checkpoint
save_checkpoint:

	;control multiple de 10 : 

	;br 0x24 ; /!\ save checkpoint anyway (for tests !)
	ldw t0, SCORE(zero)
	addi t1, zero, 0xa

	blt t0, t1, 0xc 
	sub t0, t0, t1
	addi t2, t2, 0x1
	br 0xFFFFFFF0

 	beq t0, zero, 0x8 ; if t0 != 0 (pas multiple de 10)-> ret
	add v0, zero, zero
	ret

	addi t0, zero, 0x1
	stw t0, CP_VALID(zero)

	addi t0, zero, 0x66 ; boucle de t0 = HEAD_X+HEAD_Y+TAIL_X+TAIL_Y+SCORE+GSA_LENGTH = 101 = 0x65
	addi t1, zero, 0x0

	slli t2, t1, 0x2
	ldw t3, HEAD_X(t2)
	stw t3, CP_HEAD_X(t2)
	addi t1, t1, 0x1
	blt t1, t0, 0xFFFFFFEC

	addi v0, zero, 0x1

	ret

; END: save_checkpoint


; BEGIN: restore_checkpoint
restore_checkpoint:

	;clear old GSA ne sert a rien ?
	;addi t0, zero, 0x60
	;add t1, zero, zero

	;slli t2, t1, 0x2
	;stw zero, GSA(t2)
	;addi t1, t1, 0x1
	;blt t1, t0, 0xFFFFFFF0

	ldw t0, CP_VALID(zero)

	bne t0, zero, 0x8 ; si t0 = 0 pas de checkpoint -> ne fait rien
	add v0, zero, zero
	ret

	addi t0, zero, 0x1
	stw t0, CP_VALID(zero)

	addi t0, zero, 0x66 ; boucle de t0 = HEAD_X+HEAD_Y+TAIL_X+TAIL_Y+SCORE+GSA_LENGTH = 101 = 0x65
	addi t1, zero, 0x0

	slli t2, t1, 0x2
	ldw t3, CP_HEAD_X(t2)
	stw t3, HEAD_X(t2)
	addi t1, t1, 0x1
	blt t1, t0, 0xFFFFFFEC

	addi v0, zero, 0x1
	ret



; END: restore_checkpoint


; BEGIN: blink_score
blink_score:
			; /!\ a faire chap 9


	addi t0, zero, 0x4
	sub sp, sp, t0
	stw ra, 0x0(sp)

	call turn_off_score

	call wait

	call display_score

	call wait

	call turn_off_score

	call wait

	call display_score

	ldw ra, 0x0(sp)
	addi sp, sp, 0x4

	ret 

; END: blink_score

turn_off_score : 

	add t0, zero, zero
	addi t1, zero, 0x10

	stw zero, SEVEN_SEGS(t0)
	addi t0, t0, 0x4
	bne t0, t1, 0xFFFFFFF4
		
	ret

wait:
	add t0, zero, zero ; wait est trop court 
	addi t1, zero, 0x61A8
	add t2, zero, zero
	addi t3, zero, 0xaa

	addi t0, t0, 0x1
	bne t0, t1, 0xFFFFFFF8
	add t0, zero, zero
	addi t2, t2, 0x1
	bne t2, t3, 0xFFFFFFEC

	ret

digit_map:
	.word 0xFC ; 0
	.word 0x60 ; 1 
	.word 0xDA ; 2 
	.word 0xF2 ; 3 
	.word 0x66 ; 4 
	.word 0xB6 ; 5 
	.word 0xBE ; 6 
	.word 0xE0 ; 7 
	.word 0xFE ; 8 
	.word 0xF6 ; 9