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
log_name = 'allagents.csv';
n_agents = 36; % Number of agents in the tournament (without repetition)

profiles_name = {
    'New_sporthal_util';
    'WindFarm_util';
    'Politics_util';
    };
profiles_dir = {
    'etc/templates/ANAC2015/group1-university';
    'etc/templates/ANAC2015/group2-new_sporthal';
    'etc/templates/ANAC2015/group2-politics';
    'etc/templates/ANAC2016/AgentLightSmartGrid';
    'etc/templates/ANAC2016/Maxoops';
    };


% Adding paths
addpath(log_dir);
for i = 1:size(profiles_dir, 1)
    addpath([genius_dir profiles_dir{i}]);
end

%% Reading the log
otable = readtable( log_name, 'HeaderLines', 1 );
%%
tot_sessions = size( otable.(1), 1);
n_tournaments = 3;
dom = otable.(1);
n_sessions = [size(dom(dom==0),1) size(dom(dom==1),1) size(dom(dom==2),1)];
n_agg_sessions = [0 n_sessions(1) n_sessions(1)+n_sessions(2) n_sessions(1)+n_sessions(2)+n_sessions(3)];

%times = zeros(n_sessions, n_tournaments);
rounds = {zeros(n_sessions(1), 1) zeros(n_sessions(2), 1) zeros(n_sessions(3), 1)};
nash = {zeros(n_sessions(1), 1) zeros(n_sessions(2), 1) zeros(n_sessions(3), 1)};

sessions_per_agent = zeros(n_agents, n_tournaments);
n_agreements = zeros(n_agents, n_tournaments);
nashes = {zeros(n_sessions(1), n_agents) zeros(n_sessions(2), n_agents) zeros(n_sessions(3), n_agents)};
utilities = {zeros(n_sessions(1), n_agents) zeros(n_sessions(2), n_agents) zeros(n_sessions(3), n_agents)};
utilities2 = {zeros(n_sessions(1)*3, 2) zeros(n_sessions(2)*3, 2) zeros(n_sessions(3)*3, 2)};
u_domain = {zeros(n_sessions(1), n_agents, 3) zeros(n_sessions(2), n_agents, 3) zeros(n_sessions(3), n_agents, 3)};
%u_domain = zeros(n_sessions/n_agents, n_agents, 3, n_tournaments); % 3 profiles
our_agreements = cell(3,1);

for i_tour = 1 : n_tournaments
    % Take rows in batches of n_sessions
    sub_table = otable(n_agg_sessions(i_tour)+1:n_agg_sessions(i_tour+1), :);

    %times(:, i_tour) = sub_table.(1);
    rounds{i_tour} = sub_table.(2);
    agreements = sub_table.(3);
    nash {i_tour} = sub_table.(7);
    %profiles(i_tour,:) = sub_table{1, 22:24};

    % Extracting agents names
    agentnames = sub_table{:,9:11};
    
    names = 1:n_agents;
%     an0 = regexp(agentnames(:), '@', 'split');
%     
%     names = {};
%     %names = cell(n_agents,1);
%     for j = 1:size(an0, 1)
%         same = 0;
%         for i = 1:size(names,2)
%             same = same + strcmp( an0{j}{1}, names{i});
%         end
%         if ~(same)
%             names{end+1} = an0{j}{1};
%         end
%     end

    
    % ** Assigning utilities to correct agents **
    our_agreements{i_tour} = zeros(1,3);
    
    idom = ones(n_agents, 3);

    for i=1:n_sessions(i_tour)
        an = agentnames(i,:);
        %an = regexp(agentnames(i,:), '@', 'split');
        %an = {an{1}{1} an{2}{1} an{3}{1}};
        
        ours = 0;
        for j=1:3 % Domain profiles
            for k=1:size(names,2) % Agent
                if an(j) == names(k)
                    utilities{i_tour}(i,k) = sub_table{i, 11+j};
                    utilities2{i_tour}(3*(i-1)+j,1) = sub_table{i, 11+j};
                    utilities2{i_tour}(3*(i-1)+j,2) = an(j);
                    nashes{i_tour}(i,k) = nash{i_tour}(i);

                    u_domain{i_tour}(idom(k,j), k, j) = sub_table{i, 11+j};
                    idom(k,j) = idom(k,j) + 1;
                    
                    sessions_per_agent(k,i_tour) = sessions_per_agent(k,i_tour)+1;
                    if agreements(i)==1
                        n_agreements(k,i_tour) = n_agreements(k,i_tour) + 1;
                    end
                end
                if an(j) == 1
                    ours = 1;
                end
            end
        end
        if ours == 1
            our_agreements{i_tour}(end+1, :) = sub_table{i,12:14};
        end
    end
    our_agreements{i_tour}(1,:) = [];
end

sessions_per_agent(35,:) = [1 1 1];

nsubp = 2;
figure(4), clf
%subplot(1, nsubp, 1), boxplot(times), title('Agreement times (s)'), ylim([0 180]),
class = [zeros(size(rounds{1})); ones(size(rounds{2})); 2*ones(size(rounds{3}))];
subplot(1, nsubp, 1), boxplot([rounds{1}; rounds{2}; rounds{3}], class), title('Number of rounds'),
set(gca,'YScale','log')
subplot(1, nsubp, 2), boxplot([nash{1}; nash{2}; nash{3}], class), title('Distance to Nash'),

%% Plot the results

for i_tour = 1:n_tournaments
    % Plot domain
    profile_name = profiles_name{i_tour};
    profile_nums = [1 2 3];
    
    %reg = regexp(profiles(i_tour,:), '^(\w+)(\d+).xml$', 'tokens');
    %profile_name = reg{1}{1}{1};
    %profile_nums = [str2num(reg{1}{1}{2}) str2num(reg{2}{1}{2}) str2num(reg{3}{1}{2})];

    [umax, umin, bid_space] = analyzeDomain(genius_dir, profile_name, profile_nums);
    
    % Display data
    sub_table = otable(n_agg_sessions(i_tour)+1:n_agg_sessions(i_tour+1), :);
    
    %sessions_per_agent = 36;
    figure(5), clf,
    agg_ratio = n_agreements(:,i_tour)./sessions_per_agent(:,i_tour);
    subplot(131), plot(agg_ratio), hold on, plot(agg_ratio(1)*ones(1,36)),
    title('Agreement ratio'), xlim([0.5 36]), ylim([0.9 1]), grid on, xlabel('Agent')
    
    umean = sum(utilities{i_tour}, 1)./sessions_per_agent(:,i_tour)';
    subplot(132), plot(umean.*agg_ratio'), hold on,
    plot(umean(1)*agg_ratio(1)*ones(1,36)),
    title('Mean utilities'), xlim([0.5 36]), ylim([0.7 1]), grid on, xlabel('Agent')
    
    subplot(133), plot(sum(nashes{i_tour}, 1)./sessions_per_agent(:,i_tour)'), hold on,
    plot(sum(nashes{i_tour}(:,1))/sessions_per_agent(1,i_tour)*ones(1,36)),
    title('Mean Nash distance'), xlim([0.5 36]), ylim([0 0.4]), grid on, xlabel('Agent')
    %names
    
    % Plot utilities
    figure(1),
    scatter3(sub_table{:, 12}, sub_table{:, 13}, sub_table{:, 14}, 'k+')
    scatter3(our_agreements{i_tour}(:, 1), our_agreements{i_tour}(:, 2), our_agreements{i_tour}(:, 3), 'm+')
    plot3([0 1],[0 1],[0 1])
    
    figure(2), clf,
    boxplot(utilities2{i_tour}(:,1), utilities2{i_tour}(:,2)),
    ylim([0.5 1])
    
%     figure(2), clf
%     plot(utilities{i_tour}, ':x'), legend(names, 'Location', 'south'),
%     title('Utilities through different negotiation sessions'), ylim([0 1])
%     
%     figure(3), clf
%     plot([u_domain{i_tour}(:,:,1); u_domain{i_tour}(:,:,2); u_domain{i_tour}(:,:,3)], ':x')
%     %legend(names, 'Location', 'south'), grid on,
%     set(gca, 'Xtick', 0.5:floor(n_sessions(i_tour)/3):n_sessions(i_tour))
%     title('Utilities at different domain preferences'), ylim([0 1])
%     umax;
end