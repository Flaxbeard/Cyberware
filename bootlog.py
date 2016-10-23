with open('bootlog.txt') as f:
	with open('bootloglang.txt', 'w') as op:
		count = 0
		for line in f:
			op.write("cyberware.gui.tabletBoot." + str(count) + "=" + line)
			count += 1 
