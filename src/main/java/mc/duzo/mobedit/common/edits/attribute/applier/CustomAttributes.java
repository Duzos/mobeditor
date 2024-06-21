package mc.duzo.mobedit.common.edits.attribute.applier;

import mc.duzo.mobedit.common.edits.attribute.drop.DropAttribute;

public interface CustomAttributes {
	void mobeditor$setTargetXp(int target);
	int mobeditor$getTargetXp();
	int mobeditor$getDefaultXp();
	void mobedit$setDefaultXp(int target);
	DropAttribute mobedit$getDropModifier();
	void mobedit$setDropModifier(DropAttribute drop);
}
