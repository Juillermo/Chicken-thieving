clear


% Preferences profiles

A = [ 3  2  2  1;  1 3  2 1;  2  1  4  3;  1  3 4  2;  2  3  1  1;  2  3  1  1];
B = [ 1  2  3  4;  3 2 12 1;  3  2 10  1; 12  2 1 13;  3  1  2  1; 10  3  1  2];
C = [20 15 12 10; 20 4 15 5; 25 20 10 23; 15 10 8 12; 18 15 20 15;  5 10 15 20];

wA = [0.19  0.28  0.19  0.05  0.10];
wB = [0.095 0.228 0.109 0.214 0.245];
wC = [0.264 0.190 0.223 0.058 0.097];


% Normalizing inputs

nissues = size(A,1); nvalues = size(A,2);
bid_space = nvalues ^ nissues

Am = max(A'); Amext = Am' * ones(1, nvalues);
Bm = max(B'); Bmext = Bm' * ones(1, nvalues);
Cm = max(C'); Cmext = Cm' * ones(1, nvalues);

Ar = A./Amext; Br = B./Bmext; Cr = C./Cmext;

wAm = sum(wA); wAr = wA/wAm;
wBm = sum(wB); wBr = wB/wBm;
wCm = sum(wC); wCr = wC/wCm;


% Inter-agents utilities

fA = zeros(nvalues, nvalues, nvalues, nvalues, nvalues);
fB = zeros(nvalues, nvalues, nvalues, nvalues, nvalues);
fC = zeros(nvalues, nvalues, nvalues, nvalues, nvalues);
fN = zeros(nvalues, nvalues, nvalues, nvalues, nvalues);

for i=1:nvalues
    for j=1:nvalues
        for k=1:nvalues
            for m=1:nvalues
                for n=1:nvalues
                    fA(i,j,k,m,n) = wAr * [Ar(1,i); Ar(2,j); Ar(3,k); Ar(4,m); Ar(5,n)];
                    fB(i,j,k,m,n) = wBr * [Br(1,i); Br(2,j); Br(3,k); Br(4,m); Br(5,n)];
                    fC(i,j,k,m,n) = wCr * [Cr(1,i); Cr(2,j); Cr(3,k); Cr(4,m); Cr(5,n)];
                    fN(i,j,k,m,n) = fA(i,j) * fB(i,j) * fC(i,j);
                end
            end
        end
    end
end

fAp = fA(:); fBp = fB(:); fCp = fC(:); fNp = fN(:); fTp = [fAp fBp fCp];

[vmax, imax] = max(fAp); uAmax = [fAp(imax) fBp(imax) fCp(imax)];
[vmax, imax] = max(fBp); uBmax = [fAp(imax) fBp(imax) fCp(imax)];
[vmax, imax] = max(fCp); uCmax = [fAp(imax) fBp(imax) fCp(imax)];
[vmax, imax] = max(fNp); uNmax = [fAp(imax) fBp(imax) fCp(imax)]

fmins = min(fTp'); [vmax, imax] = max(fmins); uNmin = [fAp(imax) fBp(imax) fCp(imax)]

figure(1), clf,
scatter3(fAp, fBp, fCp, 'CData', fNp), hold on,
%scatter3(fAp, fBp, fCp, fNp, fNp, 'filled')
plot3(uAmax(1),uAmax(2),uAmax(3), 'rx')
plot3(uBmax(1),uBmax(2),uBmax(3), 'rx')
plot3(uCmax(1),uCmax(2),uCmax(3), 'rx')
plot3(uNmax(1),uNmax(2),uNmax(3), 'rx')
plot3(uNmin(1),uNmin(2),uNmin(3), 'kx')
plot3(uAmax(1),uAmax(2),uAmax(3), 'rx')
plot3([uAmax(1) uBmax(1) uCmax(1) uAmax(1)],[uAmax(2) uBmax(2) uCmax(2) uAmax(2)],[uAmax(3) uBmax(3) uCmax(3) uAmax(3)])
title('Utility space')
xlabel('First agent`s utility')
ylabel('Second agent`s utility')
zlabel('Third agent`s utility')
axis([0 1 0 1 0 1])