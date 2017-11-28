% *********************************
% ** FOR READING TOURNAMENT LOGS **
% *********************************
%
% TODO: adapt for tournaments with 3+ agents, 3+ preference profiles,
% and logs with different tournaments within

clear

% Adding paths
genius_dir = '/home/juillermo/eclipse-workspace/The_nego_people/genius/';

log_dir = [genius_dir 'logs'];
log_name = 'prueba2.csv';

profiles_dir = {
    'etc/templates/ANAC2015/group1-university';
    'etc/templates/ANAC2015/group2-new_sporthal';
    };

addpath(log_dir);
for i = 1:size(profiles_dir, 1)
    addpath([genius_dir profiles_dir{i}]);
end

%% Reading the log
table = readtable( log_name );

tot_sessions = size( table.(1), 1)
n_tournaments = tot_sessions / 6;
n_sessions = 6; % Assumes 3 agents and 3 profiles

% Initializing variables
nsubp = 3; % For plotting
times = zeros(n_sessions, n_tournaments);
rounds = zeros(n_sessions, n_tournaments);
nash = zeros(n_sessions, n_tournaments);
profiles = cell(n_tournaments, 3);
utilities = zeros(n_sessions, 3, n_tournaments);
u_domain = zeros(n_sessions/3, 3, 3, n_tournaments);

for i_tour = 1 : n_tournaments
    % Take rows in batches of 6 (for 3 agents and 3 profiles)
    sub_table = table((i_tour-1)*6+1 : i_tour*6, :);

    times(:, i_tour) = sub_table.(1);
    rounds(:, i_tour) = sub_table.Round;
    nash (:, i_tour) = sub_table.(11);
    profiles(i_tour,:) = sub_table{1, 22:24};

    % ** Assigning utilities to correct agents **

    agentnames = sub_table{:,13:15};
    an0 = regexp(agentnames(1,:), '@', 'split');
    names = {an0{1}{1} an0{2}{1} an0{3}{1}};

    idom = ones(3);

    for i=1:n_sessions
        an = regexp(agentnames(i,:), '@', 'split');
        an = {an{1}{1} an{2}{1} an{3}{1}};

        for j=1:3 % Domain profile
            for k=1:3 % Agent
                if strcmp(an{j}, names{k})

                    utilities(i,k,i_tour) = sub_table{i, 15+j};

                    u_domain(idom(k,j), k, j, i_tour) = sub_table{i, 15+j};
                    idom(k,j) = idom(k,j) + 1;
                end
            end
        end
    end
end

figure(3), clf
subplot(1, nsubp, 1), boxplot(times), title('Agreement times (s)'), ylim([0 180]),
subplot(1, nsubp, 2), boxplot(rounds), title('Number of rounds'),
subplot(1, nsubp, 3), boxplot(nash), title('Distance to Nash'),

%% Plot the results

for i_tour = 1:n_tournaments
    % Plot domain
    reg = regexp(profiles(i_tour,:), '^(\w+)(\d+).xml$', 'tokens');
    
    profile_name = reg{1}{1}{1};
    profile_nums = [str2num(reg{1}{1}{2}) str2num(reg{2}{1}{2}) str2num(reg{3}{1}{2})];

    [umax, umin, bid_space] = analyzeDomain(genius_dir, profile_name, profile_nums);
    
    % Display data
    sub_table = table((i_tour-1)*6+1 : i_tour*6, :);
    
    agreements = sub_table.Agreement;
    n_agreements = 0;
    for i=1:n_sessions
        if agreements{i} == 'Yes'
            n_agreements = n_agreements + 1;
        end
    end
    n_agreements
    
    an0 = regexp(sub_table{1,13:15}, '@', 'split');
    names = {an0{1}{1} an0{2}{1} an0{3}{1}};
    
    umeans = mean(utilities(:,:,i_tour))
    
    % Plot utilities
    figure(1),
    scatter3(sub_table{:, 16}, sub_table{:, 17}, sub_table{:, 18}, 'k+')
    plot3([0 1],[0 1],[0 1])
  
    figure(2), clf
    subplot(121), plot(utilities(:,:,i_tour), ':x'), legend(names, 'Location', 'south'),
    title('Utilities through different negotiation sessions'), ylim([0 1])

    subplot(122)
    plot([u_domain(:,:,1,i_tour); u_domain(:,:,2,i_tour); u_domain(:,:,3,i_tour)], ':x')
    legend(names, 'Location', 'south'), grid on,
    set(gca, 'Xtick', 0.5:n_sessions/3:n_sessions)
    title('Utilities at different domain preferences'), ylim([0 1])
    umax;
end