.data

d1: .word 15, 13, 2, 4
d2: .asciiz "Hello World"
d3: .space 10

.text

addi $t0 $0 5
label: addi $t0 $t0 2
j 2400
sub $t0 $t1 $t6

beq $t4 $t3 5
addi $t1 $0 d1 
jal label
beq $0 $t0 label2

sub $t0, $t1, $t6
label2: sw $t3 2($t4)
lw $t4 4($t5)
sw $t4 12($t2)


halt