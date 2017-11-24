%% Reads tournament logs
% TODO: adapt for tournaments with 3+ agents

oldpath = path;
% Change to path of your logs
path('/home/juillermo/eclipse-workspace/The_nego_people/genius/logs',oldpath)

% Change to the name of the log to be read
table = readtable('Log-XmlRunner-20171121-135957.csv.csv');

nfig = 3; % For plotting

n_sessions = size(table.(1),1)

figure(1), clf,
subplot(1,nfig,1), boxplot(table.(1)),   title('Agreement times')
subplot(1,nfig,2), boxplot(table.Round), title('Number of rounds')

agreements = table.Agreement;
n_agreements = 0;
for i=1:n_sessions
    if agreements{i} == 'Yes'
        n_agreements = n_agreements + 1;
    end
end
n_agreements

subplot(1,nfig,3), boxplot(table.(11)), title('Distance to Nash')

% Assigning utilities to correct agents
agentnames = table{:,13:15};
a1n = agentnames{1,1}; a2n = agentnames{1,2}; a3n = agentnames{1,3};
a1n = regexp(a1n, '@', 'split'); a2n = regexp(a2n, '@', 'split'); a3n = regexp(a3n, '@', 'split');
a1 = a1n{1}; a2 = a2n{1}; a3 = a3n{1};
u1 = zeros(n_sessions, 1);
u2 = zeros(n_sessions, 1);
u3 = zeros(n_sessions, 1);
for i=1:n_sessions
    a1n = agentnames{i,1}; a2n = agentnames{i,2}; a3n = agentnames{i,3};
    a1n = regexp(a1n, '@', 'split'); a2n = regexp(a2n, '@', 'split'); a3n = regexp(a3n, '@', 'split');
    if strcmp(a1n{1}, a1)
        u1(i) = table{i,16};
    elseif strcmp(a1n{1}, a2)
        u2(i) = table{i,16};
    else
        u3(i) = table{i,16};
    end
    if strcmp(a2n{1}, a1)
        u1(i) = table{i,17};
    elseif strcmp(a2n{1}, a2)
        u2(i) = table{i,17};
    else
        u3(i) = table{i,17};
    end
    if strcmp(a3n{1}, a1)
        u1(i) = table{i,18};
    elseif strcmp(a3n{1}, a2)
        u2(i) = table{i,18};
    else
        u3(i) = table{i,18};
    end
end
umeans = mean([u1 u2 u3])

figure(2), clf
plot([u1 u2 u3], ':x')
legend(a1, a2, a3), grid on, set(gca, 'Xtick', 0.5:3:n_sessions+0.5)
title('Utilities through different negotiation sessions')