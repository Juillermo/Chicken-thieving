% *********************************
% ** FOR READING TOURNAMENT LOGS **
% *********************************
%
% TODO: adapt for tournaments with 3+ agents, 3+ preference profiles,
% and logs with different tournaments within

clear

%% *** PARAMETERS TO CHANGE FOR EVERY TOURNAMENT ***
genius_dir = '/home/juillermo/eclipse-workspace/The_nego_people/genius/';

log_dir = [genius_dir 'logs'];
log_name = 'bigNIGHToUT (copy).csv';
n_agents = 5; % Number of agents in the tournament (without repetition)

profiles_dir = {
    'etc/templates/ANAC2015/group1-university';
    'etc/templates/ANAC2015/group2-new_sporthal';
    'etc/templates/ANAC2015/group2-politics';
    'etc/templates/ANAC2016/AgentLightSmartGrid';
    };


% Adding paths
addpath(log_dir);
for i = 1:size(profiles_dir, 1)
    addpath([genius_dir profiles_dir{i}]);
end

%% Reading the log
table = readtable( log_name, 'HeaderLines', 1 );

tot_sessions = size( table.(1), 1);
n_sessions = 60;
n_tournaments = tot_sessions / n_sessions;

% Initializing variables
profiles = cell(n_tournaments, 3); % 3 profiles

nsubp = 3; % For plotting
times = zeros(n_sessions, n_tournaments);
rounds = zeros(n_sessions, n_tournaments);
nash = zeros(n_sessions, n_tournaments);

n_agreements = zeros(n_agents, n_tournaments);
nashes = zeros(n_sessions, n_agents, n_tournaments);
utilities = zeros(n_sessions, n_agents, n_tournaments);
u_domain = zeros(n_sessions/n_agents, n_agents, 3, n_tournaments); % 3 profiles

for i_tour = 1 : n_tournaments
    % Take rows in batches of n_sessions
    sub_table = table((i_tour-1)*n_sessions+1 : i_tour*n_sessions, :);

    times(:, i_tour) = sub_table.(1);
    rounds(:, i_tour) = sub_table.Round;
    agreements = sub_table.Agreement;
    nash (:, i_tour) = sub_table.(11);
    profiles(i_tour,:) = sub_table{1, 22:24};

    % Extracting agents names
    
    agentnames = sub_table{:,13:15};
    an0 = regexp(agentnames(:), '@', 'split');
    
    names = {};
    %names = cell(n_agents,1);
    for j = 1:size(an0, 1)
        same = 0;
        for i = 1:size(names,2)
            same = same + strcmp( an0{j}{1}, names{i});
        end
        if ~(same)
            names{end+1} = an0{j}{1};
        end
    end

    
    % ** Assigning utilities to correct agents **
    
    idom = ones(n_agents, 3);

    for i=1:n_sessions
        an = regexp(agentnames(i,:), '@', 'split');
        an = {an{1}{1} an{2}{1} an{3}{1}};

        for j=1:3 % Domain profiles
            for k=1:size(names,2) % Agent
                if strcmp(an{j}, names{k})

                    utilities(i,k,i_tour) = sub_table{i, 15+j};
                    nashes(i,k,i_tour) = nash(i,i_tour);

                    u_domain(idom(k,j), k, j, i_tour) = sub_table{i, 15+j};
                    idom(k,j) = idom(k,j) + 1;
                    
                    if strcmp( agreements{i}, 'Yes')
                        n_agreements(k,i_tour) = n_agreements(k,i_tour) + 1;
                    end
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
    sub_table = table((i_tour-1)*n_sessions+1 : i_tour*n_sessions, :);
    
    sessions_per_agent = 36;
    n_agreements(:,i_tour)'
    umeans = sum( utilities(:,:,i_tour) )/sessions_per_agent
    nashmeans = sum( nashes(:,:,i_tour) )/sessions_per_agent
    
    % Plot utilities
    figure(1),
    scatter3(sub_table{:, 16}, sub_table{:, 17}, sub_table{:, 18}, 'k+')
    plot3([0 1],[0 1],[0 1])
  
    figure(2), clf
    plot(utilities(:,:,i_tour), ':x'), legend(names, 'Location', 'south'),
    title('Utilities through different negotiation sessions'), ylim([0 1])
    
    figure(3), clf
    plot([u_domain(:,:,1,i_tour); u_domain(:,:,2,i_tour); u_domain(:,:,3,i_tour)], ':x')
    legend(names, 'Location', 'south'), grid on,
    set(gca, 'Xtick', 0.5:n_sessions/factorial(n_agents-1):n_sessions)
    title('Utilities at different domain preferences'), ylim([0 1])
    umax;
end