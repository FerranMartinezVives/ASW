Rails.application.routes.draw do
  resources :tweets do
  # Define your application routes per the DSL in https://guides.rubyonrails.org/routing.html
    member do
      put 'like'
    end
  end
  root 'tweets#index'
end
