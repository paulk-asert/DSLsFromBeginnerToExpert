% einstein.pl
% Aswin F. van Woudenberg

einstein :-
	einstein(Solution),
	write_sol(Solution).

einstein(Sol) :-
	Sol = [
		[1,N1,C1,P1,D1,S1],
		[2,N2,C2,P2,D2,S2],
		[3,N3,C3,P3,D3,S3],
		[4,N4,C4,P4,D4,S4],
		[5,N5,C5,P5,D5,S5]],
	member([_,briton,red,_,_,_],Sol),		% The Briton lives in the red house
	member([_,swede,_,dogs,_,_],Sol),     	% The Swede keeps dogs as pets
	member([_,dane,_,_,tea,_],Sol),					% The Dane drinks tea
  member([WH,_,white,_,_,_],Sol), 				% The green house is on the immediate left of the white house
  member([GH,_,green,_,_,_],Sol),
  GH =:= WH - 1,
  member([_,_,green,_,coffee,_],Sol),			% The green house owner drinks coffee
  member([_,_,_,birds,_,football],Sol),	% The person who plays football rears birds
  member([_,_,yellow,_,_,baseball],Sol), 	% The owner of the yellow house plays baseball
  member([3,_,_,_,milk,_],Sol),						% The man living in the house right in the center drinks milk
  member([1,norwegian,_,_,_,_],Sol),			% The Norwegian lives in the first house
 	member([BH,_,_,_,_,volleyball],Sol),				% The man who plays volleyball lives next to the one who keeps cats
 	member([CH,_,_,cats,_,_],Sol),
 	(BH =:= CH + 1; BH =:= CH - 1),
	member([DH,_,_,_,_,baseball],Sol),				% The man who keeps horses lives next to the one who plays baseball
 	member([HH,_,_,horses,_,_],Sol),
 	(HH =:= DH + 1; HH =:= DH - 1),
	member([_,_,_,_,beer,tennis],Sol),	% The owner who plays tennis drinks beer
	member([_,german,_,_,_,hockey],Sol),		% The German plays hockey
 	member([NH,norwegian,_,_,_,_],Sol),			% The Norwegian lives next to the blue house
 	member([BlH,_,blue,_,_,_],Sol),
 	(NH =:= BlH + 1; NH =:= BlH - 1),
 	member([SH,_,_,_,_,volleyball],Sol),				% The man who plays volleyball has a neighbor who drinks water
 	member([WaH,_,_,_,water,_],Sol),
 	(SH =:= WaH + 1; SH =:= WaH - 1),
	perm([norwegian,dane,briton,german,swede],[N1,N2,N3,N4,N5]),
	perm([yellow,blue,red,green,white],[C1,C2,C3,C4,C5]),
	perm([cats,horses,birds,fish,dogs],[P1,P2,P3,P4,P5]),
	perm([water,tea,milk,coffee,beer],[D1,D2,D3,D4,D5]),
	perm([baseball,volleyball,football,hockey,tennis],[S1,S2,S3,S4,S5]).

takeout(X,[X|R],R).
takeout(X,[F|R],[F|S]) :- takeout(X,R,S).
	
perm([X|Y],Z) :- perm(Y,W), takeout(X,Z,W).   
perm([],[]).

write_sol([A,B,C,D,E]) :-
	write('+--+------------+------------+------------+------------+------------+'),nl,
	writef('|%2L|%12L|%12L|%12L|%12L|%12L|',A),nl,
	writef('|%2L|%12L|%12L|%12L|%12L|%12L|',B),nl,
	writef('|%2L|%12L|%12L|%12L|%12L|%12L|',C),nl,
	writef('|%2L|%12L|%12L|%12L|%12L|%12L|',D),nl,
	writef('|%2L|%12L|%12L|%12L|%12L|%12L|',E),nl,
	write('+--+------------+------------+------------+------------+------------+'),nl.

