package software.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;

import hardware.datatypes.Instruction;
import software.assembly.Assembler;
import software.datatypes.Program;
import software.datatypes.Protogram;
import software.linking.LabelTable;
import software.linking.Linker;

class TestLinking {

	@Test
	void TestLabelTable() {
		Assembler asm = new Assembler();

		Protogram proto = asm.assemble("./src/software/test/test.s");

		LabelTable lt = new LabelTable(proto);
		Map<String, Integer> m = lt.getRawData();

		// add all the labels
		String res = "Labels::\n";
		for (String key : m.keySet()) {
			res = res + "\t[" + key + ", " + Integer.toHexString(m.get(key)) + "]\n";
		}

		String expected = "Labels::\n\t[label:, 400001]\n\t[label2:, 400009]\n\t[d3:, 10000010]\n\t[d1:, 10000000]\n\t[d2:, 10000004]\n";
		// System.out.println(expected);
		assertEquals(expected, res);

	}

	@Test
	void TestLinker() throws Exception {
		Assembler asm = new Assembler();
		Linker link = new Linker();

		// get the instructions and label table
		Protogram proto = asm.assemble("./src/software/test/test.s");
		LabelTable lt = new LabelTable(proto);

		// attempt to replace the labels with the actual address or immediate they
		// represent
		link.matchLabels(proto.instructions, lt);

		for (@SuppressWarnings("unused")
		Instruction ins : proto.instructions) {
			// System.out.println(ins.getASM());
		}

	}

	@Test
	void TestTotalLinker() throws Exception {
		Assembler asm = new Assembler();
		Linker link = new Linker();

		Protogram proto = asm.assemble("./src/software/test/test.s");
		Program p = link.link(proto);

		for (int i : p.staticData) {
			System.out.printf("%x ", i);
		}
		System.out.println();
		for (int i : p.bin) {
			System.out.printf("%x ", i);
		}

	}
}
