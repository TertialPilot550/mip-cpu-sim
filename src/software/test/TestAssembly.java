package software.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import hardware.MipsIsa;
import hardware.datatypes.I_Instruction;
import hardware.datatypes.Instruction;
import hardware.datatypes.J_Instruction;
import hardware.datatypes.R_Instruction;
import software.assembly.Assembler;
import software.assembly.InstructionTranslator;
import software.assembly.StaticDataFactory;
import software.datatypes.Protogram;
import software.datatypes.StaticData;

/**
 * Unit tests for assembly package
 */
class TestAssembly {

	@Test
	void testInstructionTranslation() {
		InstructionTranslator it = new InstructionTranslator();

		// parseRStatement rs, rt, rd, shamt, func
		R_Instruction t0 = new R_Instruction(1, 2, 3, 0, MipsIsa.translateInsToFunc("add"));
		try {
			assertTrue(t0.equals(it.parseStatement(t0.getASM())));
		} catch (Exception e) {
			e.printStackTrace();
		}

		// parseIStatement

		I_Instruction t1 = new I_Instruction(MipsIsa.getOpcode("addi"), 2, 3, 15);
		try {
			assertTrue(t1.getASM().equals(it.parseStatement(t1.getASM()).getASM()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		// parseJStatement
		J_Instruction t2 = new J_Instruction(2, "label");
		try {
			String statement = it.parseStatement(t2.getASM()).getASM();
			assertTrue(t2.getASM().equals(statement));
		} catch (Exception e) {
			e.printStackTrace();
		}

		// TranslateTextStatements

		List<String> statements = new ArrayList<String>();

		statements.add("add $t4 $t3 $t2");
		statements.add("jr $ra");
		statements.add("beq $t4 $t3 label");
		statements.add("add $t4, $t3, $t2");
		statements.add("sub $s3, $s2, $s1");
		statements.add("addi $t3, $t3, 10");

		try {
			List<Instruction> res0 = it.translateTextStatements(statements);

			validateInstructions(res0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void validateInstructions(List<Instruction> instructions) {
		String[] expectedResults = { "add: [add $t4 $t3 $t2] [23748640]", "jr: [jr $ra] [65075208]",
				"beq: [beq $t4 $t3 0] [292290560]", "add: [add $t4, $t3, $t2] [655392]",
				"sub: [sub $s3, $s2, $s1] [1114146]", "addi: [addi $t3, $t3, 10] [536870922]" };

		for (int i = 0; i < instructions.size(); i++) {
			Instruction ins = instructions.get(i);
			String toPrint = MipsIsa.getNeumonic(ins);
			toPrint = toPrint + ": [" + ins.getASM() + "] [" + ins.value + "]";
			// System.out.println(toPrint);
			assertTrue(expectedResults[i].equals(toPrint));
		}
	}

	@Test
	void testStaticDataFactory() {
		StaticDataFactory sd = new StaticDataFactory();

		// encode data
		try {
			int[] t0 = sd.encodeData(".space", "42");
			int sum = 0;
			for (int i = 0; i < 42; i++) {
				sum += t0[i];
			}
			assertTrue(sum == 0);

			int[] t1 = sd.encodeData(".word", "1, 3, 5, 6, 7");
			assertTrue(t1[0] == 1 && t1[1] == 3 && t1[2] == 5 && t1[3] == 6 && t1[4] == 7);

			String s = "\"Hello World\"";
			int[] t2 = sd.encodeData(".asciiz", s);
			assertEquals(Arrays.toString(t2), "[72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100, 0]");

		} catch (Exception e) {
			e.printStackTrace();
		}

		// translateData

		try {
			StaticData d0 = sd.translateData("lbl0: .word 0");
			assertEquals(Arrays.toString(d0.values), "[0]");
			assertEquals(d0.label, "lbl0:");

			StaticData d1 = sd.translateData("lbl1: .asciiz \"Hello World\"");
			assertEquals(Arrays.toString(d1.values), "[72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100, 0]");
			assertEquals(d1.label, "lbl1:");

			StaticData d2 = sd.translateData("lbl2: .space 35");
			int sum = 0;
			for (int i : d2.values) {
				sum += i;
			}
			assertEquals(sum, 0);
			assertEquals(d2.label, "lbl2:");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	void testAssembler() {

		Assembler asm = new Assembler();

		Protogram p0 = asm.assemble("test.s");

		System.out.println(p0);

		// fail("NYI");
	}

}
