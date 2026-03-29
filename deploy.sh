#!/bin/bash

# Hospital Management System Deployment Script
# Usage: ./deploy.sh [dev|prod|test]

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Default environment
ENVIRONMENT="dev"
DOCKER_COMPOSE_FILE="docker-compose.yml"
DOCKER_COMPOSE_PROD_FILE="docker-compose.prod.yml"

# Function to print colored output
print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check prerequisites
check_prerequisites() {
    print_info "Checking prerequisites..."
    
    # Check Docker
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed. Please install Docker first."
        exit 1
    fi
    
    # Check Docker Compose
    if ! command -v docker-compose &> /dev/null; then
        # Try docker compose v2
        if ! docker compose version &> /dev/null; then
            print_error "Docker Compose is not installed. Please install Docker Compose first."
            exit 1
        else
            DOCKER_COMPOSE_CMD="docker compose"
        fi
    else
        DOCKER_COMPOSE_CMD="docker-compose"
    fi
    
    # Check Maven for local builds
    if [ "$ENVIRONMENT" = "dev" ] && ! command -v mvn &> /dev/null; then
        print_warn "Maven is not installed. Using Docker builds only."
    fi
    
    print_info "Prerequisites check passed."
}

# Function to parse arguments
parse_arguments() {
    if [ $# -ge 1 ]; then
        case $1 in
            dev|development)
                ENVIRONMENT="dev"
                print_info "Setting environment to: development"
                ;;
            prod|production)
                ENVIRONMENT="prod"
                print_info "Setting environment to: production"
                ;;
            test)
                ENVIRONMENT="test"
                print_info "Setting environment to: test"
                ;;
            *)
                print_error "Unknown environment: $1"
                print_info "Usage: $0 [dev|prod|test]"
                exit 1
                ;;
        esac
    fi
}

# Function to load environment variables
load_environment() {
    print_info "Loading environment variables for $ENVIRONMENT..."
    
    if [ -f ".env.$ENVIRONMENT" ]; then
        export $(cat .env.$ENVIRONMENT | grep -v '^#' | xargs)
        print_info "Loaded environment variables from .env.$ENVIRONMENT"
    elif [ -f ".env" ]; then
        export $(cat .env | grep -v '^#' | xargs)
        print_info "Loaded environment variables from .env"
    else
        print_warn "No .env file found. Using default values."
    fi
}

# Function to build services
build_services() {
    print_info "Building services..."
    
    case $ENVIRONMENT in
        dev)
            # Build with Maven if available
            if command -v mvn &> /dev/null; then
                print_info "Building with Maven..."
                mvn clean package -DskipTests
            else
                print_info "Building with Docker..."
                $DOCKER_COMPOSE_CMD -f $DOCKER_COMPOSE_FILE build
            fi
            ;;
        prod)
            print_info "Building production images..."
            $DOCKER_COMPOSE_CMD -f $DOCKER_COMPOSE_PROD_FILE build --no-cache
            ;;
        test)
            print_info "Building test images..."
            $DOCKER_COMPOSE_CMD -f $DOCKER_COMPOSE_FILE build
            ;;
    esac
}

# Function to start services
start_services() {
    print_info "Starting services..."
    
    case $ENVIRONMENT in
        dev)
            $DOCKER_COMPOSE_CMD -f $DOCKER_COMPOSE_FILE up -d
            ;;
        prod)
            $DOCKER_COMPOSE_CMD -f $DOCKER_COMPOSE_PROD_FILE up -d
            ;;
        test)
            $DOCKER_COMPOSE_CMD -f $DOCKER_COMPOSE_FILE up -d
            ;;
    esac
    
    print_info "Services started successfully."
}

# Function to stop services
stop_services() {
    print_info "Stopping services..."
    
    case $ENVIRONMENT in
        dev)
            $DOCKER_COMPOSE_CMD -f $DOCKER_COMPOSE_FILE down
            ;;
        prod)
            $DOCKER_COMPOSE_CMD -f $DOCKER_COMPOSE_PROD_FILE down
            ;;
        test)
            $DOCKER_COMPOSE_CMD -f $DOCKER_COMPOSE_FILE down
            ;;
    esac
    
    print_info "Services stopped successfully."
}

# Function to restart services
restart_services() {
    print_info "Restarting services..."
    stop_services
    sleep 5
    start_services
}

# Function to check service health
check_health() {
    print_info "Checking service health..."
    
    # Wait for services to start
    print_info "Waiting for services to be ready..."
    sleep 30
    
    # Check API Gateway
    if curl -s http://localhost:8080/actuator/health | grep -q "UP"; then
        print_info "API Gateway: ✓ Healthy"
    else
        print_error "API Gateway: ✗ Unhealthy"
        return 1
    fi
    
    # Check Service Registry
    if curl -s http://localhost:8761/ | grep -q "Eureka"; then
        print_info "Service Registry: ✓ Healthy"
    else
        print_error "Service Registry: ✗ Unhealthy"
    fi
    
    # Check databases
    if docker ps | grep -q "postgres"; then
        print_info "PostgreSQL: ✓ Running"
    else
        print_error "PostgreSQL: ✗ Not running"
    fi
    
    if docker ps | grep -q "redis"; then
        print_info "Redis: ✓ Running"
    else
        print_error "Redis: ✗ Not running"
    fi
    
    if docker ps | grep -q "kafka"; then
        print_info "Kafka: ✓ Running"
    else
        print_error "Kafka: ✗ Not running"
    fi
    
    print_info "Health check completed."
}

# Function to view logs
view_logs() {
    print_info "Showing logs..."
    $DOCKER_COMPOSE_CMD -f $DOCKER_COMPOSE_FILE logs -f
}

# Function to run tests
run_tests() {
    print_info "Running tests..."
    
    case $ENVIRONMENT in
        dev|test)
            if command -v mvn &> /dev/null; then
                mvn test
            else
                print_warn "Maven not available. Skipping tests."
            fi
            ;;
        prod)
            print_info "Skipping tests in production environment."
            ;;
    esac
}

# Function to clean up
cleanup() {
    print_info "Cleaning up..."
    
    # Remove unused Docker resources
    docker system prune -f
    
    # Remove unused volumes
    docker volume prune -f
    
    print_info "Cleanup completed."
}

# Function to show help
show_help() {
    echo "Hospital Management System Deployment Script"
    echo ""
    echo "Usage: $0 [command] [environment]"
    echo ""
    echo "Commands:"
    echo "  start     Start services (default)"
    echo "  stop      Stop services"
    echo "  restart   Restart services"
    echo "  build     Build services"
    echo "  test      Run tests"
    echo "  health    Check service health"
    echo "  logs      View logs"
    echo "  clean     Clean up Docker resources"
    echo "  help      Show this help message"
    echo ""
    echo "Environments:"
    echo "  dev       Development (default)"
    echo "  prod      Production"
    echo "  test      Test"
    echo ""
    echo "Examples:"
    echo "  $0 start dev"
    echo "  $0 build prod"
    echo "  $0 health"
    echo "  $0 logs"
}

# Main function
main() {
    COMMAND="start"
    
    # Parse command if provided
    if [ $# -ge 1 ]; then
        COMMAND=$1
        shift
    fi
    
    # Parse environment if provided
    if [ $# -ge 1 ]; then
        parse_arguments "$1"
    else
        parse_arguments "dev"
    fi
    
    # Check prerequisites
    check_prerequisites
    
    # Load environment variables
    load_environment
    
    # Execute command
    case $COMMAND in
        start)
            build_services
            start_services
            check_health
            ;;
        stop)
            stop_services
            ;;
        restart)
            restart_services
            check_health
            ;;
        build)
            build_services
            ;;
        test)
            run_tests
            ;;
        health)
            check_health
            ;;
        logs)
            view_logs
            ;;
        clean)
            cleanup
            ;;
        help|--help|-h)
            show_help
            ;;
        *)
            print_error "Unknown command: $COMMAND"
            show_help
            exit 1
            ;;
    esac
    
    print_info "Deployment script completed successfully."
}

# Run main function with all arguments
main "$@"