function [umax, umin, bid_space, hyper_utilities, nash, eval_names] = analyzeDomain(domain_dir, domain_file, domain_nums)
% Inputs
% domain_dir: directory of the profiles files
% domain_file: name of the xml files (without number or .xml)
% domain_nums: 1x3 vector of integers with which profiles are picked

addpath(domain_dir)

profiles = cell(3,1);
for i=1:3
   profiles{i} = parseProfile([domain_file num2str(domain_nums(i)) '.xml']);
end

%% Parsing files

bid_space = 1;
n_issues = size(profiles{1}, 1);
weights = zeros(3, n_issues);
n_items = zeros(n_issues, 1);
issues = cell(n_issues, 1);
weighted_issues = cell(n_issues, 1);
eval_names = cell(n_issues, 1);

for j=1:n_issues
    n_items(j) = size(profiles{1}(j).items, 1);
    issues{j} = zeros(3, n_items(j));
   for i=1:3 % agents
       for k=1:n_items(j)
        issues{j}(i, k) = profiles{i}(j).items(k).eval;
       end
      weights(i,j) = profiles{i}(j).weight;
   end
   
   % Normalizing evaluations
   max_eval = max(issues{j}, [], 2);
   max_eval(max_eval == 0) = 1; % Avoid dividing by zero
   issues{j} = issues{j} ./ (max_eval * ones(1, n_items(j)));
   
   weighted_issues{j} = issues{j}  .*  ( weights(:,j) * ones(1, n_items(j)) );
   [ eval_names{j}{1:n_items(j)} ] = profiles{i}(j).items.value;
   bid_space = bid_space * n_items(j);
end


%% Calculating utility space

hypercubes = cell(n_issues, 1);
for j=1:n_issues
   % Change dimension
   permute_vec = 1:n_issues+1;
   permute_vec(:,[2 j+1]) = permute_vec(:, [j+1 2]);
   hyper_issue = permute(weighted_issues{j}, permute_vec);
   
   % Extrude to hypercube
   repmat_vec = [1 n_items'];
   repmat_vec(j+1) = 1;
   hypercubes{j} = repmat(hyper_issue, repmat_vec);
end

hyper_utilities = hypercubes{1};
for j=2:n_issues
    hyper_utilities = hyper_utilities + hypercubes{j};
end
utilities = reshape(hyper_utilities, 3, bid_space);

% Nash product
utilities = [utilities; utilities(1,:) .* utilities(2,:) .* utilities(3,:)];

% Max points 
[vmax, imax] = max(utilities, [], 2);
umax = utilities(:, imax);
umax = [umax; norm(umax(1:3,1)-umax(1:3,4)), norm(umax(1:3,2)-umax(1:3,4)), norm(umax(1:3,3)-umax(1:3,4)), 0];

nash = umax(1:3,4);

% Point of symmetric agreement
vmin = min(utilities);
[vmax, imax] = max(vmin);
umin = utilities(:, imax);
umin = [umin; norm(umin(1:3)-umax(1:3,4))];

%% Plotting

figure(1), clf,
scatter3(utilities(1,:), utilities(2,:), utilities(3,:), 'CData', utilities(4,:)), hold on,
plot3(umin(1),umin(2),umin(3), 'kx')
plot3(umax(1,:),umax(2,:),umax(3,:), 'rx')
plot3([umax(1,1:3) umax(1,1)],[umax(2,1:3) umax(2,1)],[umax(3,1:3) umax(3,1)])
title('Utility space')
xlabel('First agent`s utility')
ylabel('Second agent`s utility')
zlabel('Third agent`s utility')
axis([0 1 0 1 0 1])

end