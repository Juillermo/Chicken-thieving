% Negotiation domain

A = [3 2 2 1; 1 3 2 1];
wA = [0.19 0.28];
B = [1 2 3 4; 3 2 12 1];
wB = [0.095 0.228];
C = [20 15 12 10; 20 4 15 5];
wC = [0.264 0.190];

nissues = size(A,1);
nvalues = size(A,2);

Am = max(A');
Bm = max(B');
Cm = max(C');

Amext = (Am' * ones(1, size(A, 2)));
Ar = A./Amext;

Bmext = (Bm' * ones(1, size(B, 2)));
Br = B./Bmext;

Cmext = (Cm' * ones(1, size(C, 2)));
Cr = C./Cmext;

wAm = sum(wA);
wAr = wA/wAm;

wBm = sum(wB);
wBr = wB/wBm;

wCm = sum(wC);
wCr = wC/wCm;


% Intra-issues utilities

figure(1), clf,
subplot(241)
plot(1:size(A,2), Ar(1,:), 'm:x', 1:size(A,2), Ar(2,:), 'm:o')
axis([1 size(A,2) 0 1])
title('1st agent`s issues'), xlabel('values'), ylabel('utility')

subplot(242)
plot(1:size(B,2), Br(1,:), 'r:x', 1:size(B,2), Br(2,:), 'r:o')
axis([1 size(B,2) 0 1])
title('2nd agent`s issues'), xlabel('values'), ylabel('utility')

subplot(243)
plot(1:size(B,2), Cr(1,:), 'b:x', 1:size(B,2), Cr(2,:), 'b:o')
axis([1 size(B,2) 0 1])
title('2nd agent`s issues'), xlabel('values'), ylabel('utility')

subplot(244)
plot(1:size(A,2), Ar(1,:), 'm:x', 1:size(A,2), Ar(2,:), 'm:o'), hold on,
plot(1:size(B,2), Br(1,:), 'r:x', 1:size(B,2), Br(2,:), 'r:o')
plot(1:size(B,2), Cr(1,:), 'b:x', 1:size(B,2), Cr(2,:), 'b:o')
title('All agents` issues'), xlabel('values'), ylabel('utility')


% Inter-issues utilities

fA = zeros(size(A,2));
fB = zeros(size(A,2));
fC = zeros(size(A,2));
fN = zeros(nvalues);
for i=1:size(A,2)
    for j=1:size(A,2)
        fA(i,j) = wAr * [Ar(1,i); Ar(2,j)];
        fB(i,j) = wBr * [Br(1,i); Br(2,j)];
        fC(i,j) = wCr * [Cr(1,i); Cr(2,j)];
        fN(i,j) = fA(i,j) * fB(i,j) * fC(i,j);
    end
end

subplot(245), surf(fA), xlabel('1st issue'), ylabel('2nd issue')
subplot(246), surf(fB), xlabel('1st issue'), ylabel('2nd issue')
subplot(247), surf(fC), xlabel('1st issue'), ylabel('2nd issue')
subplot(248)
surf(fA, ones(size(A,2))), hold on,
surf(fB, zeros(size(A,2)))
surf(fC, 2 * ones(size(A,2)))
xlabel('1st issue'), ylabel('2nd issue')


% Inter-agents utilities

figure(2), clf,
scatter3(fA(:), fB(:), fC(:), 'CData', fN(:))
title('Utility space')
xlabel('First agent`s utility')
ylabel('Second agent`s utility')
zlabel('Third agent`s utility')
axis([0 1 0 1 0 1])