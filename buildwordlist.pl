# builds cross-linked word lists for word ladder game....
#
#	file format:
#	1st line: number of words in file
#
#	all other lines:
#	word	N	link1	link2	link3	...	linkN
#
#	indexes start at zero.

# input 
$filename = "wordlist.txt"; # source (all your words)
$wordlengthmin = 2;			# minimum length to parse
$wordlengthmax = 20;		# maximum length to parse 

# main...
for ($wordlength=$wordlengthmin; $wordlength<=$wordlengthmax; $wordlength++) {	

	print "\n\nwordlength = $wordlength\n";

	# get all words with length == $wordlength
	@linestmp = ();
	$lineidx = 0;
	open (FILE, "$filename") or die "can't open $filename";
	while ($line1=<FILE>) {
		chomp($line1);
		$len = length($line1);
		if ($len == $wordlength) {
			$linestmp[$lineidx++]=$line1;
		}
	} 

	close FILE;	

	print "raw words " . scalar @linestmp . "\n";

	# get all linkable words 
	@lines = ();
	$lineidx = 0;
	for ($i=0;$i<@linestmp;$i++) {
		$line1 = $linestmp[$i];
		$temp = 0;
		for ($j=0;$j<@linestmp;$j++) {
			$line2 = $linestmp[$j];
			if ($i != $j && &isValidJump($line1,$line2)) {
				$temp = 1;
				$j = 1 + @linestmp;	#break
			}
		}
		if ($temp==1) {
			$lines[$lineidx++]=$line1;
		}
	}

	print "linked words " . scalar @lines . "\n";

	# link words and save output
	$filename2 = "wordlist$wordlength\.txt";
	$status = 0;

	open (FILE, ">$filename2") or die "can't open $filename2";
	print FILE scalar @lines . "\n";	# length of file

	for ($i=0; $i<@lines; $i++) {
		$line1 = $lines[$i];			
		$temp = "";
		$numberoflinkedwords = 0;

		for ($j=0; $j<@lines; $j++) {
			$line2 = $lines[$j];
			if ($i != $j && &isValidJump($line1,$line2)) {
				$numberoflinkedwords++;
				$temp =  $temp . "\t$j"; # index from 0
			}
		}

		if($temp gt "") {

			# print progress meter
			if (++$status > 99) { 		
				$status = 0;	
				print "$i $line1\n";	#status
			}

			# print line to file
			# file format: 	word	N	link1	link2	...	linkN
			print FILE "$line1\t$numberoflinkedwords$temp\n";	# no line numbers
		}
	}
	close FILE;
}


# test if two words are kin
sub isValidJump {
	my ($line1tmp,$line2tmp) = @_;
	my $len = length($line1tmp);
	my $unmatch = 0;

	for ($n = 0; $n < $len ; $n++ ) {
		$a = substr ($line1tmp, $n, 1); 
		$b = substr ($line2tmp, $n, 1); 	
		if (!($a eq $b)) {
			$unmatch++;
			if ($unmatch>1) {
				$n = $len;	#break loop
			}
		}
	}
	return ($unmatch == 1);
}

